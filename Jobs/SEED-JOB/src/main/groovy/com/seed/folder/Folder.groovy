package com.seed.folder

class Folder {
    def name
    def folderDisplayName
    def folderDesc
    def view

    Folder(name) {
        this.name = name
        this.folderDisplayName = name
    }

    void setFolderDisplayName(folderDisplayName) {
        this.folderDisplayName = folderDisplayName
    }

    void setFolderDesc(folderDesc) {
        this.folderDesc = folderDesc
    }

    void setView(view) {
        this.view = view
    }

    def createFolder(dsl) {
        dsl.folder(name) {
            displayName(folderDisplayName)
            description(folderDesc)
        }
        if (view) {
            view.createView()
        }
    }
}
