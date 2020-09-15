package com.github.jchanghong

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


internal fun configurationPlugin(project: Project) {
    log2("configurationPlugin()", project)
    project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            it.kotlinOptions.jvmTarget = "1.8"
            it.kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
            log2("it.kotlinOptions.jvmTarget=\"1.8\"", project)
        }
    }
    project.pluginManager.withPlugin("java-library") {
        project.extensions.findByType(JavaPluginExtension::class.java)?.let {
            it.withSourcesJar()
            it.withJavadocJar()
            log2("withJavadocJar withSourcesJar", project)
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
            log2("java.targetCompatibility=\"1.8\"", project)
        }
    }
    project.pluginManager.withPlugin("io.spring.dependency-management") {
        val managementExtension =
                project.extensions.findByType(io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension::class.java)
        managementExtension?.imports {
            it.mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
        log2("apply plugin boot DependencyManagementPlugin", project)
    }
    project.pluginManager.withPlugin("name.remal.maven-publish-nexus-staging") {
        project.tasks.findByName("releaseNexusRepositories")?.dependsOn("publish")
    }
    setmavenpublish(project)
}

internal fun setmavenpublish(project: Project) {
    project.pluginManager.withPlugin("maven-publish") {
        project.tasks.findByName("releaseNexusRepositories")?.dependsOn("publish")
        val publishingExtension = project.extensions.findByType(PublishingExtension::class.java)
        if (publishingExtension != null) {
            val mavenPublication = publishingExtension.publications.maybeCreate("JCH", MavenPublication::class.java)
            mavenPublication.from(project.components.findByName("java"))
            mavenPublication.pom.apply {
                name.set("kotlin-lib")
                description.set("kotlin java tools")
                url.set("http://www.example.com/library")
                licenses {

                    it.license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
                    url.set("git@github.com:jchanghong/utils.git")
                }
            }
            publishingExtension.repositories.apply {
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
            val signingExtension = project.extensions.findByType(SigningExtension::class.java)
            if (signingExtension != null) {
                signingExtension.sign(mavenPublication)
                log2("add mavenPublication JCH", project)
            }
        }
    }
}