pipeline {
    agent any
    parameters {
        string(name: 'VERSION')
    }
    environment {
        DOCKER_CREDS = credentials('docker')
    }
    stages {
        stage('Test') {
            steps {
                echo "Hello Jenkins"
            }
        }
        stage('Build') {
            steps {
                script {
                    echo "Cloning Image from repo..."
                    //sh "git pull https://github.com/zoltanvacz/Devops-Test-App.git"
                    sh "cd Devops-Test-App"
                    //sh "docker build -t zoltanvacz/devops-test-app:1.2 ."
                    docker.build("zoltanvacz/devops-test-app:1.2", ".")
                    //sh "docker image ls zoltanvacz/devops-test-app:1.2"
                    //sh "docker login -u $DOCKER_CREDS_USR -p $DOCKER_CREDS_PSW"
                    //sh "docker push zoltanvacz/devops-test-app:1.2"
                }
            }
        }
    }
}
