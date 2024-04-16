package com.seed.view

class ViewFactory {
    static getView(Map config, dsl) {
        def view = new View(dsl, config.name)
        if (config?.desc) {
            view.setViewDesc(config?.desc)
        }
        view
    }

    static setJobsIncluded(job) {
        View.setJobsIncluded(job)
    }

    static getJobsIncluded() {
        return View.jobsIncluded
    }
}
