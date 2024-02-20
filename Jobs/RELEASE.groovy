def getAgents() {
    def agents = []
    def nodes = Jenkins.instance.nodes
    nodes.each { node ->
        if (node instanceof hudson.slaves.DumbSlave) {
            agents.add(node.name)
        }
    }
    return agents
}

pipeline {
    agent any
    parameters {
        choice(name: 'Application', choices: ['Devops-Test-App'], description: 'Select application')
        string(name: 'VERSION', description: 'Enter version')
        choice(name: 'Agent', choices: getAgents(), description: 'Select agent')
    }
    environment {
        DOCKER_CREDS = credentials('docker')
        GITHUB_TOKEN = credentials('GitHubToken')
    }
    stages {
        stage('Clone App Repo') {
            steps {
                script {
                    def AppRepo = "${env:Application}-Config"
                    when {
                        expression { return !fileExists(AppRepo) }
                        steps {
                            sh "git clone https://github.com/zoltanvacz/${AppRepo}.git"
                        }
                    }
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
