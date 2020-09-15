package com.github.jchanghong

import org.gradle.api.Project
import org.gradle.plugins.signing.SigningPlugin


internal fun addPlugin(project: Project) {
    log2("addPlugin()", project)
    project.pluginManager.withPlugin("java") {
        log2("has java plugin, add DependencyManagementPlugin kotlin dokka", project)
        project.pluginManager.apply(io.spring.gradle.dependencymanagement.DependencyManagementPlugin::class.java)
        project.pluginManager.apply(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper::class.java)
        project.pluginManager.apply(org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin::class.java)
        project.pluginManager.apply(org.jetbrains.kotlin.noarg.gradle.KotlinJpaSubplugin::class.java)
        project.pluginManager.apply(org.jetbrains.dokka.gradle.DokkaPlugin::class.java)
    }
    project.pluginManager.withPlugin("application") {
        log2("has plugin application,add SpringBootPlugin", project)
        project.pluginManager.apply(org.springframework.boot.gradle.plugin.SpringBootPlugin::class.java)
    }
    project.pluginManager.withPlugin("java-library") {
        log2("has plugin java-library add SigningPlugin", project)
        project.pluginManager.apply(SigningPlugin::class.java)
    }
    project.pluginManager.withPlugin("maven-publish") {
        log2("has plugin maven-publish add MavenPublishNexusStagingPlugin", project)
        project.pluginManager.apply(name.remal.gradle_plugins.plugins.publish.nexus_staging.MavenPublishNexusStagingPlugin::class.java)
    }
}