package com.seed.view

class View {
    def viewName
    def viewDesc
    static def jobsIncluded = []
    def re
    def statusFilter
    def dsl

    View(dsl, viewName) {
        this.dsl = dsl
        this.viewName = viewName
    }

    void setViewDesc(viewDesc) {
        this.viewDesc = viewDesc
    }

    static void setJobsIncluded(job) {
        jobsIncluded << job
    }

    void setRe(re) {
        this.re = re
    }

    void setStatusFilter(statusFilter) {
        this.statusFilter = statusFilter
    }

    def createView() {
        dsl.listView(viewName) {
            description(viewDesc)
            filterBuildQueue()
            filterExecutors()
            jobs {
                jobsIncluded.each {
                    name(it)
                }
                if (re) {
                    regex(re)
                }
            }
            if (statusFilter) {
                jobFilters {
                    status {
                        status(statusFilter)
                    }
                }
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    }
}
