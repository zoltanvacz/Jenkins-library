//@Grab('org.yaml:snakeyaml:1.33')
@Library('Jenkins-library@main') _

import com.seed.jobs.JobFactory
import com.seed.utils.Parser
import hudson.FilePath
import org.yaml.snakeyaml.Yaml
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*


pipeline {
    agent any
    stages {
        stage('Reading YAML files') {
            steps {
                script {
                    def workspaceDir = pwd()
                    def yamlFiles = findFiles(glob: "${workspaceDir}/config/appservice/*.yaml")
                    def env = Jenkins.instance.getGlobalNodeProperties()[0].getEnvVars()
                    envId = env["ENV_ID"] ? env["ENV_ID"] : "all"
                    def currentBuild = Thread.currentThread().executable
                    def cause = currentBuild.getCauses()[0]
                    def des = cause.shortDescription.split(" ")
                    def userId = des[des.length - 1]

                    yamlFiles.each { file ->
                        def yaml = new Yaml().load(file.readToString())
                        try {
                            appTargetEnv = yaml.spec["targetEnv"]? yaml.spec["targetEnv"] : "all"
                            appOwners = yaml.spec["owners"]? yaml.spec["owners"] : userId        
                            if ((appTargetEnv == "all" || appTargetEnv.toUpperCase().contains(envId.toUpperCase())) && appOwners.toUpperCase().contains(userId.toUpperCase())) {
                                jobCreate = true
                            } else {
                                jobCreate = false
                            }
                            println("create: "+file.toString()+"--"+ appTargetEnv+"--" + appOwners+"--" + jobCreate +"--"+ userId + "-env:-"+ envId)
                            if (jobCreate) { 
                                switch (yaml.kind) {
                                    case 'MultibranchJob':
                                        def multiBranchJob = Parser.parseMultiBranchJob(this, yaml)
                                        JobFactory.addJob(multiBranchJob)
                                        break
                                    case 'PipelineJob':
                                        def piplineJob = Parser.parsePipelineJob(this, yaml)
                                        JobFactory.addJob(piplineJob)
                                        break
                                    case 'GitHubOrgJob':
                                        def ghOrgJob = Parser.parseGitHubOrgJob(this, yaml)
                                        JobFactory.addJob(ghOrgJob)
                                        break
                                    default:
                                        throw new Exception('incorrect job type')
                                }
                            }        
                        } catch (e) {
                            println(e.message)
                            e.printStackTrace()
                        }
                    }
                    JobFactory.createJobs()
                }
            }
        }
    }
}
//env.WORKSPACE = "/home/jenkins/agent"
def workspaceDir = pwd()
def yamlFiles = findFiles(glob: "${workspaceDir}/config/appservice/*.yaml")
def env = Jenkins.instance.getGlobalNodeProperties()[0].getEnvVars()
envId = env["ENV_ID"] ? env["ENV_ID"] : "all"
def currentBuild = Thread.currentThread().executable
def cause = currentBuild.getCauses()[0]
def des = cause.shortDescription.split(" ")
def userId = des[des.length - 1]

yamlFiles.each { file ->
    def yaml = new Yaml().load(file.readToString())
    try {
        appTargetEnv = yaml.spec["targetEnv"]? yaml.spec["targetEnv"] : "all"
        appOwners = yaml.spec["owners"]? yaml.spec["owners"] : userId        
        if ((appTargetEnv == "all" || appTargetEnv.toUpperCase().contains(envId.toUpperCase())) && appOwners.toUpperCase().contains(userId.toUpperCase())) {
            jobCreate = true
        } else {
            jobCreate = false
        }
        println("create: "+file.toString()+"--"+ appTargetEnv+"--" + appOwners+"--" + jobCreate +"--"+ userId + "-env:-"+ envId)
        if (jobCreate) { 
            switch (yaml.kind) {
                case 'MultibranchJob':
                    def multiBranchJob = Parser.parseMultiBranchJob(this, yaml)
                    JobFactory.addJob(multiBranchJob)
                    break
                case 'PipelineJob':
                    def piplineJob = Parser.parsePipelineJob(this, yaml)
                    JobFactory.addJob(piplineJob)
                    break
                case 'GitHubOrgJob':
                    def ghOrgJob = Parser.parseGitHubOrgJob(this, yaml)
                    JobFactory.addJob(ghOrgJob)
                    break
                default:
                    throw new Exception('incorrect job type')
            }
        }        
    } catch (e) {
        println(e.message)
        e.printStackTrace()
    }
}
JobFactory.createJobs()
