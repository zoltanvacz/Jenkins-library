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
                    //sh "git clone https://github.com/zoltanvacz/Devops-Test-App.git"
                    sh "docker build -t zoltanvacz/devops-test-app:1.2 -f Devops-Test-App/Dockerfile ."
                    sh "docker image ls zoltanvacz/devops-test-app:1.2"
                    sh "docker login -u $DOCKER_CREDS_USR -p $DOCKER_CREDS_PSW"
                    sh "docker push zoltanvacz/devops-test-app:1.2"
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    //sh "git clone https://github.com/zoltanvacz/Devops-Test-App-Config.git"
                    dir('Devops-Test-App-Config') {
                        sh "pwd"
                        sh "git checkout -b ${VERSION}"
                        sh "sed -i 's|image: zoltanvacz/devops-test-app:1.0|image: zoltanvacz/devops-test-app:${VERSION}|g' dev/deployment.yaml"
                        //File conf = new File('/dev/deployment.yaml')
                        //println conf.text
                        sh "git add ."
                        sh "git commit -m 'releasing new version ${VERSION}'"
                        sh "git push"
                    }
                    sh "git checkout main"
                    sh "git branch -d ${VERSION}"
                }
            }
        }
    }
}
