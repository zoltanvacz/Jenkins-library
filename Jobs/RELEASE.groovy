pipeline {
    agent any
    parameters {
        choice(name: 'Application', choices: ['Devops-Test-App'], description: 'Select application')
        string(name: 'VERSION', description: 'Enter version')
    }
    environment {
        DOCKER_CREDS = credentials('docker')
        GITHUB_TOKEN = credentials('GitHubToken')
    }
    stages {
        stage('Clone App Repo') {
            script {
                def AppRepo = "${env.Application}-Config"
            }
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
                    echo "Deploying new version ${VERSION}..."
                }
            }
        }
    }
}
