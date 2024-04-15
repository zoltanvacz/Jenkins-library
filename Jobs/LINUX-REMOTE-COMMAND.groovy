pipeline {
    agent any
    parameters {
        string(name: 'HOST', description: 'Enter hostname', defaultValue: '172.31.32.1')
        string(name: 'COMMAND', description: 'Enter command', defaultValue: 'ls -l')
        string(name: 'USER', description: 'Enter username', defaultValue: 'root')
        password(name: 'SSH_PASSWORD', description: 'Enter password')
    }
    stages {
        stage('Execute remote command') {
            steps {
                script {
                    def remote = [:]
                    remote.name = 'linux'
                    remote.host = "${env.HOST}"
                    remote.user = "${params.USER}"
                    remote.allowAnyHosts = true

                    withCredentials([usernamePassword(credentialsId: '', variable: 'SSH_PASSWORD', value: params.PASSWORD)]) {
                        remote.password = env.SSH_PASSWORD  
                        if (!remote.password) {
                            error("Failed to fetch password")
                        }
                        echo remote.password
                        sshCommand remote: remote, command: "sh ~/scripts/script.sh"
                    }
                 //sshScript remote: remote, script: "/scripts/script.sh"
                    
                }
            }
        }
    }
}