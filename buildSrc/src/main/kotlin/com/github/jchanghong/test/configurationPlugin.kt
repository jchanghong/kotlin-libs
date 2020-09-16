package com.github.jchanghong.test

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


internal fun configurationPlugin(project: Project, myExtension: JchPluginExtension) {
    log2("configurationPlugin()", project, myExtension.logInfo)
    project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            it.kotlinOptions.jvmTarget = "1.8"
            it.kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
            log2("it.kotlinOptions.jvmTarget=\"1.8\"", project, myExtension.logInfo)
        }
    }
    project.pluginManager.withPlugin("java-library") {
        project.extensions.findByType(JavaPluginExtension::class.java)?.let {
            it.withSourcesJar()
            it.withJavadocJar()
            log2("withJavadocJar withSourcesJar", project, myExtension.logInfo)
        }
        project.tasks.withType(Javadoc::class.java) {
            if (JavaVersion.current().isJava9Compatible) {
                (it.options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            }
        }
    }
    project.pluginManager.withPlugin("java") {
        project.tasks.withType(JavaCompile::class.java).configureEach {
            it.targetCompatibility = "1.8"
            log2("java.targetCompatibility=\"1.8\"", project, myExtension.logInfo)
        }
    }
    project.pluginManager.withPlugin("io.spring.dependency-management") {
        val managementExtension =
            project.extensions.findByType(io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension::class.java)
        managementExtension?.imports {
            it.mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
        log2("apply plugin boot DependencyManagementPlugin", project, myExtension.logInfo)
    }
    project.pluginManager.withPlugin("name.remal.maven-publish-nexus-staging") {
        project.tasks.findByName("releaseNexusRepositories")?.dependsOn("publish")
    }
    setmavenpublish(project, myExtension)
}

internal fun setmavenpublish(project: Project, myExtension: JchPluginExtension) {
//    project.pluginManager.withPlugin("name.remal.maven-publish-nexus-staging") {
//        project.tasks.findByName("releaseNexusRepositories")?.dependsOn("publish")
//    }
    project.pluginManager.withPlugin("maven-publish") {
        val publishingExtension = project.extensions.findByType(PublishingExtension::class.java)
        if (publishingExtension != null) {
            val mavenPublication = publishingExtension.publications.maybeCreate("JCH", MavenPublication::class.java)
            mavenPublication.from(project.components.findByName("java"))
            mavenPublication.pom {
                setMavenPOM(it)
            }
            publishingExtension.repositories {
                setPublishRepositoryHandler(it, project)
            }
            val signingExtension = project.extensions.findByType(SigningExtension::class.java)
            if (signingExtension != null) {
                signingExtension.sign(mavenPublication)
                log2("add mavenPublication JCH", project, myExtension.logInfo)
            }
        }
    }
}

internal fun setPublishRepositoryHandler(repositoryHandler: RepositoryHandler?, project: Project) {
    repositoryHandler?.apply {
        maven {
            it.name = "sona"
            val releasesRepoUrl = project.uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = project.uri("${project.buildDir}/repos/snapshots")
//            val releasesRepoUrl = uri("$buildDir/repos/releases")

            it.url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            if (it.url.toString().startsWith("http")) {
                it.isAllowInsecureProtocol = true
                it.credentials {
                    it.username = "jchanghong"
                    it.password = "!b58r5gsHu*0"
                }
            }
        }
    }
}

internal fun setMavenPOM(mavenPom: MavenPom?) {
    mavenPom?.apply {
        name.set("kotlin-lib")
        description.set("kotlin java tools")
        url.set("http://www.example.com/library")
        licenses {

            it.license {
                it.name.set("The Apache License, Version 2.0")
                it.url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            it.developer {
                it.email.set("3200601e@qq.com")
            }
        }
        scm {
            it.connection.set("scm:git:https://github.com/jchanghong/utils.git")
            it.developerConnection.set("scm:git:git@github.com:jchanghong/utils.git")
            it.url.set("git@github.com:jchanghong/utils.git")
        }
    }
}
