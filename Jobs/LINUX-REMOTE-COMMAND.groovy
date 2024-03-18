pipeline {
    agent any
    parameters {
        string(name: 'HOST', description: 'Enter hostname', defaultValue: '172.31.32.1')
        string(name: 'COMMAND', description: 'Enter command', defaultValue: 'ls -l')
        string(name: 'USER', description: 'Enter username', defaultValue: 'root')
        password(name: 'PASSWORD', description: 'Enter password', defaultValue: 'password')
    }
    stages {
        stage('Execute remote command') {
            steps {
                script {
                    def remote = [:]
                    remote.name = 'linux'
                    remote.host = "${env.HOST}"
                    remote.user = "${params.USER}"
                    remote.password = '$params.PASSWORD'
                    remote.allowAnyHosts = true

                    echo remote.password

                    //sshScript remote: remote, script: "/scripts/script.sh"
                    sshCommand remote: remote, command: "sh ~/scripts/script.sh"
                }
            }
        }
    }
}