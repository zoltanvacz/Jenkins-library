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
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    dir('Devops-Test-App-Config') {
                        sh "git pull"
                        //def branchExists = sh "git rev-parse --verify origin/release-${VERSION}"
                        def branchExists sh (script: "git checkout release-${VERSION}", returnStatus: true)
                        echo branchExists
                        if (branchExists != "fatal: Needed a single revision") {
                            sh "git checkout release-${VERSION}"
                            } 
                        else {
                            sh "git checkout -b release-${VERSION}"
                        }
                    }
                    echo "Deploying new version ${VERSION}..."

                }
            }
        }
    }
}
