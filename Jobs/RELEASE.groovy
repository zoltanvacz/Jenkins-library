def getAgents() {
    def agents = []
    def nodes = Jenkins.instance.nodes
    for (def i = 0; i < 5; i++) {
        agents.add("Agent" + i)
    }
    return agents
}

pipeline {
    agent any
    environment {
        DOCKER_CREDS = credentials('docker')
        GITHUB_TOKEN = credentials('GitHubToken')
    }
    stages {
        stage('Jenkins UI') {
            steps {
                script {
                    properties { [
                        parameters([
                             [$class: 'ChoiceParameter',
                                        choiceType: 'PT_MULTI_SELECT',
                                        filterLength: 1,
                                        filterable: true,
                                        name: 'SVCNAME',
                                        script: [
                                            $class: 'GroovyScript',
                                            fallbackScript: [
                                                classpath: [],
                                                sandbox: true,
                                                script:
                                                    "return['Could not fetch the services']"
                                            ],
                                            script: [
                                                classpath: [],
                                                sandbox: true,
                                                script: 'return ' + getAgents()
                                            ]
                                        ]
                                    ],
                        choice(name: 'Application', choices: ['Devops-Test-App'], description: 'Select application'),
                        string(name: 'VERSION', description: 'Enter version'),
                        choice(name: 'Agent', choices: getAgents(), description: 'Select agent')
                        ])
                    ] }

                    }
                }
            }
        }
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
