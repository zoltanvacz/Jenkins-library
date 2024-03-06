pipeline {
    agent any
    parameters {
        choice(name: 'APPLICATION', choices: ['Devops-Test-App'], description: 'Select application')
        string(name: 'VERSION', description: 'Enter version', defaultValue: '1.0')
        choice(name: 'REPO', choices: ['zoltanvacz'], description: 'Select repo')
    }
    environment {
        DOCKER_CREDS = credentials('docker')
        AppRepo = "${env.Application}"
        Repo = "${env.REPO}"
        APP_NAME = "${env.APPLICATION}".toLowerCase()
    }
    stages {
        stage('Clone App Repo') {
            when {
                expression { return !fileExists(AppRepo) }
            }
            steps {
                script {
                    echo "Cloning Image from repo..."
                    sh "git clone https://github.com/${Repo}/${AppRepo}.git"
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    sh "git config --global user.email 'jenkins@jenkins.com'"
                    sh "git config --global user.name 'jenkins'"
                    sh "docker build -t ${Repo}/${APP_NAME}:${env.VERSION} -f ${AppRepo}/Dockerfile ."
                    sh "docker image ls ${Repo}/${APP_NAME}:${env.VERSION}"
                    sh "docker login -u $DOCKER_CREDS_USR -p $DOCKER_CREDS_PSW"
                    sh "docker push ${Repo}/${APP_NAME}:${env.VERSION}"
                    sh "rm -r ${AppRepo}"
                }
            }
        }
    }
}
