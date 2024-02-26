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
        GITHUB_CREDS = credentials('GITHUB_CREDS')
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
                        sh "git pull origin main"
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
                    dir("${AppRepo}") {
                        def deploymentFile = 'dev/deployment.yaml'
                        def data = readYaml file: deploymentFile
                        data.spec.template.spec.containers[0].image = "zoltanvacz/devops-test-app:${VERSION}"
                        sh "rm -f ${deploymentFile}"
                        writeYaml file: deploymentFile, data: data
                        sh "cat ${deploymentFile}"

                        //sh "git config --global user.name '${GITHUB_CREDS_USR}'"
                        //sh "git config --global credential.helper '!echo password=${GITHUB_CREDS_PSW}; echo'"
                        //sh "git remote set-url origin https://github.com/zoltanvacz/Devops-Test-App-Config.git"
                        sh "git remote rm origin"
                        sh "git remote add origin 'git@github.com:zoltanvacz/Devops-Test-App-Config.git'"
                        
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
        stage('Merge PR') {
            steps {
                script {
                    echo "Create PR..."
                    def payload = """
                    {
                        "title": "Release ${VERSION}",
                        "head": "release-${VERSION}",
                        "base": "main",
                        "body": "Release ${VERSION}",
                        "maintainer_can_modify": true
                    }
                    """
                    def response = httpRequest(
                        acceptType: 'APPLICATION_JSON',
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'POST',
                        requestBody: payload,
                        url: "https://api.github.com/repos/zoltanvacz/Devops-Test-App-Config/pulls" ,
                        customHeaders: [[name: 'Authorization', value: "Bearer ${GITHUB_TOKEN}"]]
                    )
                    def jsonResponse = readJSON text: response.content
                    def prNumber = jsonResponse.number
                    echo "Pull request created successfully. PR Number: ${prNumber}"

                    echo "Merge PR..."
                    def mergePayload = """
                    {
                        "commit_title": "Merge release-${VERSION} to main",
                        "commit_message": "Merge release-${VERSION} to main",
                        "sha": "release-${VERSION}"
                    }
                    """
                    def mergeResponse = httpRequest(
                        acceptType: 'APPLICATION_JSON',
                        contentType: 'APPLICATION_JSON',
                        httpMode: 'PUT',
                        requestBody: mergePayload,
                        url: "https://api.github.com/repos/zoltanvacz/Devops-Test-App-Config/pulls/${prNumber}/merge" ,
                        customHeaders: [[name: 'Authorization', value: "token ${GITHUB_TOKEN}"]]
                    )
                }
            }
        }
        stage('Cleanup env') {
            steps {
                script {
                    sh "rm -r ${AppRepo}"
                }
            }
        }
    }
}
