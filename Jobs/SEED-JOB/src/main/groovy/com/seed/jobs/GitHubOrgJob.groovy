package com.seed.jobs

import com.seed.folder.Folder

class GitHubOrgJob implements Job {
//    final def BUILD_NUMBER_TO_KEEP = 100
    def jobDsl
    def jobName
    def jobDisplayName
    def jobDesc
    def ghOwner
    def ghUrl = 'https://github.wdf.sap.corp'
    def ghApi = 'api/v3'
    def credId
    def excludeBranches = ''
    def includeBranches = '*'
//    def buildNumToKeep = BUILD_NUMBER_TO_KEEP
    def buildNumToKeep
    def jenkinsfilename = 'Jenkinsfile'
    def ghBranchDisId = 3
    def ghPRDisId = 1
    def ghForkDisId = 1
    def repoRegex = '.*'
    def sourceDisFilter = 'regex'
    def excludeRepos = ''
    def includeRepos = '*'
    private Folder[] folders

    GitHubOrgJob(jobDsl, jobName, ghOwner, credId) {
        this.jobDsl = jobDsl
        this.jobName = jobName
        this.jobDisplayName = jobName
        this.ghOwner = ghOwner
        this.credId = credId
    }

    void setJobDisplayName(jobDisplayName) {
        this.jobDisplayName = jobDisplayName
    }

    void setJobDesc(jobDesc) {
        this.jobDesc = jobDesc
    }

    void setGhApi(ghApi) {
        this.ghApi = ghApi
    }

    void setExcludeBranches(excludeBranches) {
        this.excludeBranches = excludeBranches
    }

    void setIncludeBranches(includeBranches) {
        this.includeBranches = includeBranches
    }

    void setBuildNumToKeep(buildNumToKeep) {
        this.buildNumToKeep = buildNumToKeep
    }

    void setJenkinsfilename(jenkinsfilename) {
        this.jenkinsfilename = jenkinsfilename
    }

    void setGhUrl(ghUrl) {
        this.ghUrl = ghUrl
    }

    void setGhBranchDisId(ghBranchDisId) {
        this.ghBranchDisId = ghBranchDisId
    }

    void setGhPRDisId(ghPRDisId) {
        this.ghPRDisId = ghPRDisId
    }

    void setRepoRegex(repoRegex) {
        this.repoRegex = repoRegex
    }

    void setSourceDisFilter(sourceDisFilter) {
        if (sourceDisFilter != 'regex' && sourceDisFilter != 'wildcard') {
            throw new Exception('incorrect filter setting, the value should be "regex" or "wildcard"')
        }
        this.sourceDisFilter = sourceDisFilter
    }

    void setExcludeRepos(excludeRepos) {
        this.excludeRepos = excludeRepos
    }

    void setIncludeRepos(includeRepos) {
        this.includeRepos = includeRepos
    }

    void setGhForkDisId(ghForkDisId) {
        this.ghForkDisId = ghForkDisId
    }

    @Override
    void setFolders(folders) {
        this.folders = folders
    }

    @Override
    def createJob() {
        def name = jobName
        if (folders) {
            folders.each { folder ->
                folder.createFolder(jobDsl)
            }
            name = "${folders[-1].name}/${jobName}"
        }
        jobDsl.organizationFolder(name) {
            // Sets a description for the item.
            description(jobDesc)
            // Sets the name to display instead of the actual name.
            displayName(jobDisplayName)
            // Sets the organizations in this folder.
            organizations {
                github {
                    // Specify the name of the GitHub Organization or GitHub User Account.
                    repoOwner(ghOwner)
                    // The server to connect to.
                    apiUri("${ghUrl}/${ghApi}")
                    // Credentials used to scan branches and pull requests, check out sources and mark commit statuses.
                    credentialsId(credId)
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
                        if (sourceDisFilter == 'regex') {
                            sourceRegexFilter {
                                // A Java regular expression to restrict the project names.
                                regex(repoRegex)
                            }
                        } else {
                            sourceWildcardFilter {
                                // Space-separated list of project name patterns to consider.
                                includes(includeRepos)
                                // Space-separated list of project name patterns to ignore even if matched by the includes list.
                                excludes(excludeRepos)
                            }
                        }
                    }
                }
            }
            // Sets the orphaned branch strategy.
            if (buildNumToKeep) {
                orphanedItemStrategy {
                    discardOldItems {
                        numToKeep(buildNumToKeep)
                    }
                }
            }
            // Sets the project factories for this folder.
            projectFactories {
                workflowMultiBranchProjectFactory {
                    // Relative location within the checkout of your Pipeline script.
                    scriptPath(jenkinsfilename)
                }
            }
            configure {
                def traits = it / navigators / 'org.jenkinsci.plugins.github__branch__source.GitHubSCMNavigator' / traits
                traits << 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait' {
                    strategyId(ghForkDisId)
                    trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
                }
            }
        }
    }
}
