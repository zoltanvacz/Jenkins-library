package com.seed.folder

class FolderFactory {
    static getFolder(Map config) {
        def folder = new Folder(config.name)
        if (config?.displayName) {
            folder.setFolderDisplayName(config?.displayName)
        }
        if (config?.desc) {
            folder.setFolderDesc(config?.desc)
        }
        folder
    }
}