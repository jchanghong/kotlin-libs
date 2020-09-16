package com.github.jchanghong.test

import org.gradle.api.Project

internal fun setRepositories(project: Project, myExtension: JchPluginExtension) {
    log2("setRepositories() http://maven.aliyun.com/nexus/content/groups/public", project, myExtension.logInfo)
//          maven("http://maven.aliyun.com/nexus/content/groups/public")
//        jcenter()
//        maven("http://af.hikvision.com.cn:80/artifactory/maven-down/")
    project.repositories.mavenLocal()
    project.repositories.maven { it.setUrl("http://maven.aliyun.com/nexus/content/groups/public") }
    project.repositories.jcenter()
    project.repositories.maven {
        it.setUrl("http://af.hikvision.com.cn:80/artifactory/maven-down/")
    }
}