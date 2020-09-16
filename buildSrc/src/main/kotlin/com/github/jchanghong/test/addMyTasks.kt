package com.github.jchanghong.test

import org.gradle.api.Project

internal fun addMyTasks(project: Project, myExtension: JchPluginExtension) {
    for (softwareComponent in project.components) {
        log2(softwareComponent.name, project, myExtension.logInfo)
    }
//    log2("addMyTasks()", project)
    // Add the 'testplugin' extension object

//
    // Register a task
//    project.tasks.register("testplugin") { task ->
//        task.doLast {
//            println(" ${myExtension.message} Hello from plugin 'com.github.jchanghong.testplugin'")
//        }
//    }
}