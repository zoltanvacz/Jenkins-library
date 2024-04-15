package com.seed.jobs

import com.seed.folder.Folder

class PipelineJob implements Job {
//    final def BUILD_NUMBER_TO_KEEP = 20
    def jobName
    def jobDesc
    def credId
    def remoteName = 'origin'
    def remoteUrl
    boolean disabledJob = false
    def jobDisplayName
    def jobDsl
    def lightweightCheckout = true
//    def buildNumberToKeep = BUILD_NUMBER_TO_KEEP
    private def branch = 'master'
    def buildNumberToKeep
    private def jenkinsfileName = 'Jenkinsfile'
    private Folder[] folders

    private def parameters = []

    PipelineJob(jobDsl, jobName, credId, remoteUrl) {
        this.jobName = jobName
        this.credId = credId
        this.remoteUrl = remoteUrl
        this.jobDisplayName = jobName
        this.jobDsl = jobDsl
    }

    def getJenkinsfileName() {
        return jenkinsfileName
    }

    void setBranch(branch) {
        this.branch = branch
    }

    void setJobDesc(jobDesc) {
        this.jobDesc = jobDesc
    }

    void setRemoteName(remoteName) {
        this.remoteName = remoteName
    }

    void setDisabledJob(boolean disabledJob) {
        this.disabledJob = disabledJob
    }

    void setJobDisplayName(jobDisplayName) {
        this.jobDisplayName = jobDisplayName
    }

    void setLightweightCheckout(lightweightCheckout) {
        this.lightweightCheckout = lightweightCheckout
    }

    @Override
    void setFolders(folders) {
        this.folders = folders
    }

    void setJenkinsfileName(jenkinsfileName) {
        this.jenkinsfileName = jenkinsfileName
    }

    void setBuildNumberToKeep(buildNumberToKeep) {
        this.buildNumberToKeep = buildNumberToKeep
    }

    void setParameters(parameters) {
        this.parameters = parameters
    }

    @Override
    def createJob() {
        def fullName = jobName
        if (folders) {
            folders.each { folder ->
                folder.createFolder(jobDsl)
            }
            fullName = "${folders[-1].name}/${jobName}"
        }
        jobDsl.pipelineJob(fullName) {
            // Adds a workflow definition.
            definition {
                cpsScm {
                    scm {
                        git {
                            remote {
                                // Sets credentials for authentication with the remote repository.
                                credentials(credId)
                                // Sets a name for the remote.
                                name(remoteName)
                                // Sets the remote URL.
                                url(remoteUrl)
                            }
                            // Specify the branches to examine for changes and to build.
                            branches(branch)
                        }
                    }
                    // If selected, try to obtain the Pipeline script contents directly from the SCM without performing a full checkout.
                    lightweight(lightweightCheckout)
                    // Sets the relative location of the pipeline script within the source code repository.
                    scriptPath(jenkinsfileName)
                }
            }
            // Sets a description for the item.
            description(jobDesc)
            // Disables the job, so that no new builds will be executed until the project is re-enabled.
            disabled(disabledJob)
            // Sets the name to display instead of the actual name.
            displayName(jobDisplayName)
            // Manages how long to keep records of the builds.
            if (buildNumberToKeep) {
                logRotator {
                    // If specified, build records are only kept up to this number of days.
//                daysToKeep(int daysToKeep)
                    // If specified, only up to this number of build records are kept.
                    numToKeep(buildNumberToKeep)
                }
            }
            parameters {
                parameters.each{ parameter -> 
                    switch(parameter.type) {
                        case "choice":
                            choiceParam(parameter.name, parameter.options)
                            break
                        case "string":
                            stringParam(parameter.name)
                            break
                        case "boolean":
                            booleanParam(parameter.name, parameter.value)
                            break
                        default:
                            break    
                    }
                
                }
            }
        }
    }
}
