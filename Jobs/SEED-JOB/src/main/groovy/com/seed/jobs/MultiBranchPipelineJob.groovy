package com.seed.jobs

import com.seed.folder.Folder

class MultiBranchPipelineJob implements Job {
//    final def BUILD_NUMBER_TO_KEEP = 20
    def jobName
    def jobId
    def jobDesc = ''
    def githubRepoOwner
    def repoName
    def gitHubUrl = 'https://github.wdf.sap.corp'
    def gitHubApi = 'api/v3'
    def scanCredId
    def checkoutCredId
    def includeBranches = '*'
    def excludeBranches = ''
    def buildNumToKeep
//    def buildNumToKeep = BUILD_NUMBER_TO_KEEP
    def jobDsl
    boolean exception = false
    def exceptionBranchName = '**'
    def exceptionName
    private def jenkinsfileName = 'Jenkinsfile'
    private boolean skipPRBuild = false
    private Folder[] folders
    def ghBranchDisId = 3
    def ghPRDisId = 1
    def ghForkDisId = 1
    private boolean cloneOptionTrait_noTags = true
    def notificationContextTrait_contextLabel = ''
    private boolean notificationContextTrait_typeSuffix = false
    def libraries = []

    MultiBranchPipelineJob(jobDsl, jobName, repoOwner, repoName, scanId) {
        this.jobDsl = jobDsl
        this.jobName = jobName
        this.githubRepoOwner = repoOwner
        this.repoName = repoName
        this.scanCredId = scanId
        this.jobId = jobName
    }

    @Override
    void setFolders(folders) {
        this.folders = folders
    }

    void setSkipPRBuild(boolean skipPRBuild) {
        this.skipPRBuild = skipPRBuild
    }

    void setJenkinsfileName(jenkinsfileName) {
        this.jenkinsfileName = jenkinsfileName
    }

    void setJobDesc(jobDesc) {
        this.jobDesc = jobDesc
    }

    void setGithubRepoOwner(githubRepoOwner) {
        this.githubRepoOwner = githubRepoOwner
    }

    void setRepoName(repoName) {
        this.repoName = repoName
    }

    void setGitHubUrl(gitHubUrl) {
        this.gitHubUrl = gitHubUrl
    }

    void setScanCredId(scanCredId) {
        this.scanCredId = scanCredId
    }

    void setCheckoutCredId(checkoutCredId) {
        this.checkoutCredId = checkoutCredId
    }

    void setIncludeBranches(includeBranches) {
        this.includeBranches = includeBranches
    }

    void setExcludeBranches(excludeBranches) {
        this.excludeBranches = excludeBranches
    }

    void setBuildNumToKeep(buildNumToKeep) {
        this.buildNumToKeep = buildNumToKeep
    }

    void setException(boolean exception) {
        this.exception = exception
    }

    void setExceptionBranchName(exceptionBranchName) {
        this.exceptionBranchName = exceptionBranchName
    }

    void setExceptionName(exceptionName) {
        this.exceptionName = exceptionName
    }

    void setGhBranchDisId(ghBranchDisId) {
        this.ghBranchDisId = ghBranchDisId
    }

    void setGhPRDisId(ghPRDisId) {
        this.ghPRDisId = ghPRDisId
    }

    void setGhForkDisId(ghForkDisId) {
        this.ghForkDisId = ghForkDisId
    }

    void setcloneOptionTrait_noTags(cloneOptionTrait_noTags) {
        this.cloneOptionTrait_noTags = cloneOptionTrait_noTags
    }

    void setnotificationContextTrait_contextLabel(notificationContextTrait_contextLabel) {
        this.notificationContextTrait_contextLabel = notificationContextTrait_contextLabel
    }

    void setnotificationContextTrait_typeSuffix(boolean notificationContextTrait_typeSuffix) {
        this.notificationContextTrait_typeSuffix = notificationContextTrait_typeSuffix
    }

    void setLibraries(def libraries) {
        this.libraries = libraries
    }

    @Override
    def createJob() {
        def newJobName = jobName
        if (folders) {
            folders.each { folder ->
                folder.createFolder(jobDsl)
            }
            newJobName = "${folders[-1].name}/${jobName}"
        }    

        jobDsl.multibranchPipelineJob(newJobName) {
            branchSources {
                branchSource {
                    source {
                        github {
                            repoOwner(githubRepoOwner)
                            repository(repoName)
                            repositoryUrl("${gitHubUrl}/${repoName}.git")
                            configuredByUrl(true)
                            // The server to connect to.
                            apiUri("${gitHubUrl}/${gitHubApi}")
                            id(jobId)
                            // Credentials used to scan branches and pull requests, check out sources and mark commit statuses.
                            credentialsId(scanCredId)
                            // The behaviours control what is discovered from the GitHub repository.
                            traits {
                                gitHubBranchDiscovery {
                                    // Determines which branches are discovered.
                                    strategyId(ghBranchDisId)
                                }
                                // Discovers pull requests where the origin repository is the same as the target repository.
                                gitHubPullRequestDiscovery {
                                    // Determines how pull requests are discovered: Merging the pull request with the current target branch revision Discover each pull request once with the discovered revision corresponding to the result of merging with the current revision of the target branch.
                                    strategyId(ghPRDisId)
                                }
                                headWildcardFilter {
                                    // Space-separated list of name patterns to consider.
                                    includes(includeBranches)
                                    // Space-separated list of name patterns to ignore even if matched by the includes list.
                                    excludes(excludeBranches)
                                }
                                cloneOptionTrait {
                                    extension {
                                        // Deselect this to perform a clone without tags, saving time and disk space when you just want to access what is specified by the refspec.
                                        noTags(cloneOptionTrait_noTags)
                                        // Specify a folder containing a repository that will be used by Git as a reference during clone operations.
                                        reference("")
                                        // Perform shallow clone, so that git will not download the history of the project, saving time and disk space when you just want to access the latest version of a repository.
                                        shallow(false)
                                        // Specify a timeout (in minutes) for clone and fetch operations.
                                        timeout(10)
                                    }
                                }
                                if (notificationContextTrait_typeSuffix) {
                                    notificationContextTrait {
                                        // The text of the context label for Github status notifications.
                                        contextLabel(notificationContextTrait_contextLabel)
                                        // Appends the relevant suffix to the context label based on the build type.
                                        typeSuffix(notificationContextTrait_typeSuffix)
                                    }
                                }
                            }
                        }
                    }
                    if (exception) {
                        strategy {
//                        defaultBranchPropertyStrategy {}
                            namedExceptionsBranchPropertyStrategy {
                                namedExceptions {
                                    named {
                                        name(exceptionBranchName)
                                        props {
//                                        buildRetentionBranchProperty {}
// This setting allows users to change the default durability mode for running Pipelines.
//                                        durabilityHintBranchProperty {}
// Suppresses the normal SCM commit trigger coming from branch indexing.
                                            if (exceptionName == 'Suppress automatic SCM triggering') {
                                                noTriggerBranchProperty()
                                            }
// Enforces a minimum time between builds based on the desired maximum rate.
//                                        rateLimitBranchProperty {}
// This property will cause a job for a pull request (PR-*) to be triggered immediately when a comment is placed on the PR in GitHub.
//                                        triggerPRCommentBranchProperty {}
// This property will cause a job for a pull request (PR-*) to be triggered immediately when a review is made on the PR in GitHub.
//                                        triggerPRReviewBranchProperty()
// This property will cause a job for a pull request (PR-*) to be triggered immediately when the PR title or description is edited in GitHub.
//                                        triggerPRUpdateBranchProperty()
// Indicates that the branch contains code changes from authors who do not otherwise have the write access to the repository.
//                                        untrustedBranchProperty {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            factory {
                workflowBranchProjectFactory {
                    scriptPath(jenkinsfileName)
                }
            }
            orphanedItemStrategy {
                discardOldItems {
                     //numToKeep(buildNumToKeep)
                }
            }
            configure {
                def traits = it / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
                traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
                    strategyId(ghForkDisId)
                    trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
                }
            }
            properties {
                folderLibraries {
                    libraries {
                        for (def oneLib: libraries) {
                            libraryConfiguration {
                                name(oneLib.name)
                                defaultVersion(oneLib.defaultVersion)
                                implicit(false)
                                allowVersionOverride(true)
                                includeInChangesets(true)
                                retriever {
                                    modernSCM {
                                        scm {
                                            git {
                                                remote(oneLib.gitRepository)
                                                credentialsId(oneLib.gitHubCredentialId)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
