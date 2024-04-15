package com.seed.jobs

class JobFactory {
    static List<Job> jobs = []

    static MultiBranchPipelineJob getMultiBranchPipelineJob(Map config, dsl) {
        def job = new MultiBranchPipelineJob(dsl, config.jobName, config.repoOwner, config.repoName, config.scanId)
        if (config?.gitHubUrl) {
            job.setGitHubUrl(config?.gitHubUrl)
        }
        if (config?.includes) {
            job.setIncludeBranches(config?.includes)
        }
        if (config?.excludes) {
            job.setExcludeBranches(config?.excludes)
        }
        if (config?.jobDesc) {
            job.setJobDesc(config?.jobDesc)
        }
        if (config?.num) {
            job.setBuildNumToKeep(config?.num)
        }
        if (config?.jenkinsfile) {
            job.setJenkinsfileName(config?.jenkinsfile)
        }
        if (config?.libraries) {
            job.setLibraries(config?.libraries)
        }
        if (config?.exception) {
            def exception = config.exception as Map
            job.setException(true)
            job.setExceptionName(exception.name)
            if (exception?.exceptionBranch) {
                job.setExceptionBranchName(exception?.exceptionBranch)
            }
        }
        if (config?.ghBranchDisId) {
            job.setGhBranchDisId(config?.ghBranchDisId)
        }
        if (config?.ghPRDisId) {
            job.setGhPRDisId(config?.ghPRDisId)
        }
        if (config?.ghForkDisId) {
            job.setGhForkDisId(config?.ghForkDisId)
        }
        if (config?.cloneOptionTrait_noTags == false || true) {
            job.setcloneOptionTrait_noTags(config?.cloneOptionTrait_noTags)
        }
        if (config?.notificationContextTrait_contextLabel) {
            job.setnotificationContextTrait_contextLabel(config?.notificationContextTrait_contextLabel)
        }
        if (config?.notificationContextTrait_typeSuffix) {
            job.setnotificationContextTrait_typeSuffix(config?.notificationContextTrait_typeSuffix)
        }
        job
    }

    static getPipelineJob(Map config, dsl) {
        def job = new PipelineJob(dsl, config.jobName, config.credId, config.remoteUrl)
        if (config?.jobDesc) {
            job.setJobDesc(config.jobDesc)
        }
        if (config?.jobDisplayName) {
            job.setJobDisplayName(config.jobDisplayName)
        }
        if (config?.disabledJob) {
            job.setDisabledJob(config.disabledJob as boolean)
        }
        if (config?.buildNumToKeep) {
            job.setBuildNumberToKeep(config?.buildNumToKeep)
        }
        if (config?.jenkinsfile) {
            job.setJenkinsfileName(config?.jenkinsfile)
        }
        if (config?.branch) {
            job.setBranch(config?.branch)
        }
        if (config?.lightweightCheckout) {
            job.setLightweightCheckout(config?.lightweightCheckout)
        }
        if (config?.parameters) {
            job.setParameters(config?.parameters)
        }
        job
    }

    static getGHOrgJob(Map config, dsl) {
        def job = new GitHubOrgJob(dsl, config.jobName, config.ghOwner, config.credId)
        if (config?.gitHubUrl) {
            job.setGhUrl(config?.gitHubUrl)
        }
        if (config?.branchInclude) {
            job.setIncludeBranches(config?.branchInclude)
        }
        if (config?.branchExclude) {
            job.setExcludeBranches(config?.branchExclude)
        }
        if (config?.jobDesc) {
            job.setJobDesc(config?.jobDesc)
        }
        if (config?.num) {
            job.setBuildNumToKeep(config?.num)
        }
        if (config?.jenkinsfile) {
            job.setJenkinsfilename(config?.jenkinsfile)
        }
        if (config?.sourceDisFilter) {
            def filter = config?.sourceDisFilter
            job.setSourceDisFilter(filter)
            if (filter == 'regex' && config?.repoRegex) {
                job.setRepoRegex(config?.repoRegex)
            } else if (filter == 'wildcard') {
                if (config?.repoInclude) {
                    job.setIncludeRepos(config?.repoInclude)
                }
                if (config?.repoExclude) {
                    job.setExcludeRepos(config?.repoExclude)
                }
            }
        }
        if (config?.ghBranchDisId) {
            job.setGhBranchDisId(config?.ghBranchDisId)
        }
        if (config?.ghPRDisId) {
            job.setGhPRDisId(config?.ghPRDisId)
        }
        if (config?.ghForkDisId) {
            job.setGhForkDisId(config?.ghForkDisId)
        }
        job
    }

    static createJob(Job job) {
        job.createJob()
    }

    static addJob(Job job) {
        jobs << job
    }

    static createJobs() {
        jobs.each {
            createJob(it)
        }
    }
}