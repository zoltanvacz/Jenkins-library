def branchExists
pipeline {
    agent any
    parameters {
        choice(name: 'Application', choices: ['Devops-Test-App'], description: 'Select application')
        string(name: 'VERSION', description: 'Enter version', defaultValue: '1.0')
    }
    environment {
        DOCKER_CREDS = credentials('docker')
        GITHUB_TOKEN = credentials('GitHubToken')
        AppRepo = "${env.Application}-Config"
    }
    stages {
        stage('Clone App Repo') {
            when {
                expression { return !fileExists(AppRepo) }
            }
            steps {
                script {
                    sh "git clone https://github.com/zoltanvacz/${AppRepo}.git"
                    //sh "git clone git@github.com:zoltanvacz/${AppRepo}.git"
                }
            }
        }
        stage('Create Release Branch') {
            steps {
                script {
                    dir('Devops-Test-App-Config') {
                        sh "git checkout main"
                        sh "git pull"
                        //def branchExists = sh "git rev-parse --verify origin/release-${VERSION}"
                        branchExists = (sh (script: "git rev-parse --verify origin/release-${VERSION}", returnStatus: true) == 0)
                        if (branchExists) {
                            echo "Branch already exists!"
                            sh "git checkout release-${VERSION}"
                        } else {
                            sh "git checkout -b release-${VERSION}"
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    echo "Deploying new version ${VERSION}..."
                    dir('Devops-Test-App-Config') {
                        def deploymentFile = 'dev/deployment.yaml'
                        def data = readYaml file: deploymentFile
                        data.spec.template.spec.containers[0].image = "zoltanvacz/devops-test-app:${VERSION}"
                        sh "rm -f ${deploymentFile}"
                        writeYaml file: deploymentFile, data: data

                        sh "git config --global user.email 'vaczzoltan12@gmail.com"
                        sh "git config --global user.name 'zoltanvacz'"
                        sh "git remote set-url origin https://github.com/zoltanvacz/Devops-Test-App-Config.git"
                        sh "git add ."
                        sh "git commit -m 'releasing new version ${VERSION}'"
                        if(branchExists) {
                            sh "git push"
                        } else {
                            sh "git push --set-upstream origin release-${VERSION}"
                        }
                    }
                }
            }
        }
    }
}
