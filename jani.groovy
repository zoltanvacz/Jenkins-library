pipeline {
    agent any
    stages {
        stage("Disable job") {
            steps {
                script {
                    //Copy disable job
                    echo "Disable job"
                    def job = Jenkins.instance.getItem("test")

                    if(job) {
                        if(job.disabled) {
                            echo "Job already disabled"
                        }
                        else {
                            job.disabled = true
                            job.save()
                            echo "Job disabled"
                        }
                    }
                    else {
                        echo "Job not found"
                    }
                }
            }
        }
        stage("PS Script") {
            input {
                message "Mehet?"
                ok "Proceed"
            }
            steps {
                script {
                    echo "PS Script"
                }
            }
        }
        stage("Enable job") {
            steps {
                script {
                    //Copy enable job
                    echo "Enable job"
                    def job = Jenkins.instance.getItem("test")

                    if(job) {
                        if(!job.disabled) {
                            echo "Job already enabled"
                        }
                        else {
                            job.disabled = false
                            job.save()
                            echo "Job enabled"
                        }
                    }
                    else {
                        echo "Job not found"
                    }
                }
            }
        }
    }
}
