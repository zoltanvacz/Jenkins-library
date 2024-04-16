package com.seed.utils

import com.seed.folder.FolderFactory
import com.seed.jobs.Job
import com.seed.jobs.JobFactory
import com.seed.view.ViewFactory

class Parser {
    static def parseMultiBranchJob(dsl, yaml) {
        def jobName = yaml.metadata.name
        def id = yaml.metadata?.id
        def description = yaml.metadata?.description
        def buildNumToKeep = yaml.spec?.buildNumToKeep
        def repoOwner = yaml.spec.repoOwner
        def repoName = yaml.spec.repoName
        def gitHubUrl = yaml.spec.gitHubUrl
        def scanId = yaml.spec.gitHubCredentialId
        def includes = yaml.spec?.branchInclude
        def excludes = yaml.spec?.branchExclude
        def jenkinsfile = yaml.spec?.jenkinsfile
        def ghBranchDisId = yaml.spec?.ghBranchDisId
        def ghPRDisId = yaml.spec?.ghPRDisId
        def ghForkDisId = yaml.spec?.ghForkDisId
        def cloneOptionTrait_noTags = yaml.spec?.cloneOptionTrait_noTags
        def notificationContextTrait_contextLabel = yaml.spec?.notificationContextTrait_contextLabel
        def notificationContextTrait_typeSuffix = yaml.spec?.notificationContextTrait_typeSuffix
        def exception = yaml.spec?.exception
        def libraries = yaml.spec?.libraries
        def multiBranchJob = JobFactory.getMultiBranchPipelineJob(dsl, jobName: jobName, repoOwner: repoOwner, repoName: repoName, gitHubUrl: gitHubUrl, scanId: scanId, jobId: id, includes: includes,
                excludes: excludes, jobDesc: description, num: buildNumToKeep, jenkinsfile: jenkinsfile, ghBranchDisId: ghBranchDisId, ghPRDisId: ghPRDisId, ghForkDisId: ghForkDisId, exception: exception, cloneOptionTrait_noTags: cloneOptionTrait_noTags, notificationContextTrait_contextLabel: notificationContextTrait_contextLabel, notificationContextTrait_typeSuffix: notificationContextTrait_typeSuffix, libraries: libraries)
        parseFolderAndView(dsl, yaml.spec?.folders, multiBranchJob)
        multiBranchJob
    }

    static def parsePipelineJob(dsl, yaml) {
        def jobName = yaml.metadata.name
        def description = yaml.metadata?.description
        def buildNumToKeep = yaml.spec?.buildNumToKeep
        def url = yaml.spec.remoteUrl
        def ghCredential = yaml.spec.gitHubCredentialId
        def jenkinsfile = yaml.spec?.jenkinsfile
        def branch = yaml.spec?.githubBranch
        def lightweightCheckout = yaml.spec?.lightweightCheckout as boolean
        def jobDisplayName = yaml.spec?.jobDisplayName
        def disableJob = yaml.spec?.disableJob as boolean
        def parameters = yaml.spec?.parameters
        def pipelineJob = JobFactory.getPipelineJob(dsl, jobName: jobName, jobDesc: description, remoteUrl: url, credId: ghCredential, branch: branch, lightweightCheckout: lightweightCheckout,
                jobDisplayName: jobDisplayName, disabledJob: disableJob, jenkinsfile: jenkinsfile, buildNumToKeep: buildNumToKeep, parameters: parameters)
        parseFolderAndView(dsl, yaml.spec?.folders, pipelineJob)
        pipelineJob
    }

    static def parseGitHubOrgJob(dsl, yaml) {
        def jobName = yaml.metadata.name
        def description = yaml.metadata?.description
        def buildNumToKeep = yaml.spec?.buildNumToKeep
        def ghOwner = yaml.spec.gitHubOwner
        def gitHubUrl = yaml.spec.gitHubUrl
        def ghCredId = yaml.spec.gitHubCredentialId
        def include = yaml.spec?.branchInclude
        def exclude = yaml.spec?.branchExclude
        def jenkinsfile = yaml.spec?.jenkinsfile
        def sourceDisFilter = yaml.spec?.sourceDisFilter
        def regex = yaml.spec?.repoRegex
        def repoInclude = yaml.spec?.repoInclude
        def repoExclude = yaml.spec?.repoExclude
        def ghBranchDisId = yaml.spec?.ghBranchDisId
        def ghPRDisId = yaml.spec?.ghPRDisId
        def ghForkDisId = yaml.spec?.ghForkDisId
        def ghOrgJob = JobFactory.getGHOrgJob(dsl, jobName: jobName, ghOwner: ghOwner, gitHubUrl: gitHubUrl, credId: ghCredId, branchInclude: include,
                branchExclude: exclude, jobDesc: description, num: buildNumToKeep, jenkinsfile: jenkinsfile, sourceDisFilter: sourceDisFilter, repoRegex: regex, repoInclude: repoInclude,
                repoExclude: repoExclude, ghBranchDisId: ghBranchDisId, ghPRDisId: ghPRDisId, ghForkDisId: ghForkDisId)
        parseFolderAndView(dsl, yaml.spec?.folders, ghOrgJob)
        ghOrgJob
    }

    static def parseFolderAndView(dsl, folders, Job job) {
        def result = []
        if (folders) {
            folders.each { folder ->
                def folderName = folder?.name
                if (folderName) {
                    def newFolder = FolderFactory.getFolder(name: folderName, desc: folder?.description,
                            displayName: new StringTokenizer(folderName, '/').toList()[-1])
                    def folderView = folder?.view
                    if (folder?.view) {
                        def newView = ViewFactory.getView(dsl, name: folderView?.name, desc: folderView?.description)
                        ViewFactory.setJobsIncluded(folderName)
                        newFolder.setView(newView)
                    }
                    result << newFolder
                }
            }
        }
        job.setFolders(result)
        job
    }
}
