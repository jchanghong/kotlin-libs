/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.jchanghong

import cn.hutool.core.io.resource.ResourceUtil
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun log2(log: Any?, project: Project) {
    project.logger.quiet("set project【${project.name}】:${log.toString()}")
}
open class GreetingPluginExtension {
    var message = "1Hello from GreetingPlugin"
}
/**
 * A simple 'hello world' plugin.
 * 一个工程一个对象实例
 */
class JchGradlePlugin: Plugin<Project> {
  lateinit  var myExtension:GreetingPluginExtension
    override fun apply(project: Project) {
        myExtension= project.extensions.create("jch", GreetingPluginExtension::class.java)
        setPropertie(project)
        setRepositories(project)
        addPlugin(project)
        project.afterEvaluate { afterEvaluate(it) }
    }
    private fun afterEvaluate(project: Project) {
        log2("afterEvaluate()",project)
        configurationPlugin(project)
        setdependencies(project)
        addMyTasks(project)
    }

    private fun setdependencies(project: Project) {
        log2("setdependencies()",project)
        val allDepens = project.configurations.filter {
            it.name=="api"||it.name=="implementation"||it.name=="compile"||it.name=="testApi"||it.name=="testImplementation"
        }.flatMap {conf->
            conf.dependencies.map { "${it.group}:${it.name}" }
        }.toHashSet()
        addtestImplementation("org.springframework.boot:spring-boot-starter-test","2.3.3.RELEASE",project, allDepens)
        project.configurations.all {
            it.resolutionStrategy.eachDependency {
                if (it.requested.group == "org.jetbrains.kotlin") {
                    it.useVersion("1.4.10")
                }
                if (it.requested.group == "com.squareup.okhttp3"&&it.requested.name=="okhttp") {
                    it.useVersion("4.9.0")
                }
            }
        }
    }

    fun addtestImplementation(key: String,version:String,project: Project,allDepens:HashSet<String>) {
        if (key !in allDepens) {
            project.dependencies.add("testImplementation","${key}:${version}")
        }
    }
    fun addImplementation(key: String,version:String,project: Project,allDepens:HashSet<String>) {
        if (key !in allDepens) {
            project.dependencies.add("implementation","${key}:${version}")
        }
    }
    /** 签名插件设置*/
    private fun setPropertie(project: Project) {
        val readUtf8Str = ResourceUtil.readUtf8Str("asc.asc")
        project.pluginManager.withPlugin("signing"){
            project.extensions.findByType(SigningExtension::class.java)?.let {
                it.useInMemoryPgpKeys(readUtf8Str,"123buyaodaohao")
                log2("set signing ",project)
            }
        }
    }

    private fun addMyTasks(project: Project) {
        log2("addMyTasks()",project)
        // Add the 'testplugin' extension object


        // Register a task
        project.tasks.register("testplugin") { task ->
            task.doLast {
                println(" ${myExtension.message} Hello from plugin 'com.github.jchanghong.testplugin'")
            }
        }
    }

    private fun configurationPlugin(project: Project) {
        log2("configurationPlugin()",project)
        project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm"){
            project.tasks.withType(KotlinCompile::class.java).configureEach {
                it.kotlinOptions.jvmTarget="1.8"
                it.kotlinOptions.freeCompilerArgs=listOf("-Xjsr305=strict")
                log2("it.kotlinOptions.jvmTarget=\"1.8\"",project)
            }
        }
        project.pluginManager.withPlugin("java-library"){
            project.extensions.findByType(JavaPluginExtension::class.java)?.let {
                it.withSourcesJar()
                it.withJavadocJar()
                log2("withJavadocJar withSourcesJar",project)
            }
            project.tasks.withType(Javadoc::class.java) {
                if (JavaVersion.current().isJava9Compatible) {
                    (it.options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
                }
            }
        }
        project.pluginManager.withPlugin("java"){
            project.tasks.withType(JavaCompile::class.java).configureEach {
                it.targetCompatibility="1.8"
                log2("it.targetCompatibility=\"1.8\"",project)
            }
        }
      project.pluginManager.withPlugin("io.spring.dependency-management"){
          val findByType =
              project.extensions.findByType(io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension::class.java)
          findByType?.imports {
              it.mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
          }
          log2("apply plugin boot DependencyManagementPlugin",project)
      }
        project.pluginManager.withPlugin("name.remal.maven-publish-nexus-staging"){
            project.tasks.findByName("releaseNexusRepositories")?.dependsOn("publish")
        }
        setmavenpublish(project)
    }

    private fun setmavenpublish(project: Project) {
        project.pluginManager.withPlugin("maven-publish"){
            project.tasks.findByName("releaseNexusRepositories")?.dependsOn("publish")
            val extension = project.extensions.findByType(PublishingExtension::class.java)
            if (extension != null) {
                val mavenPublication = extension.publications.maybeCreate("MAVEN", MavenPublication::class.java)
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
                extension.repositories.apply {
                    maven {
                        it.name="sona"
                        val releasesRepoUrl = project.uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                        val snapshotsRepoUrl = project.uri("${project.buildDir}/repos/snapshots")
//            val releasesRepoUrl = uri("$buildDir/repos/releases")

                        it. url = if (project.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                        if (it.url.toString().startsWith("http")) {
                            it.isAllowInsecureProtocol=true
                            it.credentials {
                                it.username="jchanghong"
                                it.password="!b58r5gsHu*0"
                            }
                        }
                    }
                }
                val signingExtension = project.extensions.findByType(SigningExtension::class.java)
                if (signingExtension != null) {
                    signingExtension.sign(mavenPublication)
                    log2("add mavenPublication",project)
                }
            }
        }
    }

    private fun addPlugin(project: Project) {
        log2("addPlugin()",project)
        project.pluginManager.withPlugin("java"){
            log2("has java plugin, add DependencyManagementPlugin kotlin dokka",project)
            project.pluginManager.apply(io.spring.gradle.dependencymanagement.DependencyManagementPlugin::class.java)
            project.pluginManager.apply(org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper::class.java)
            project.pluginManager.apply(org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin::class.java)
            project.pluginManager.apply(org.jetbrains.kotlin.noarg.gradle.KotlinJpaSubplugin::class.java)
            project.pluginManager.apply(org.jetbrains.dokka.gradle.DokkaPlugin::class.java)
        }
        project.pluginManager.withPlugin("application"){
            log2("has plugin application,add SpringBootPlugin",project)
            project.pluginManager.apply(org.springframework.boot.gradle.plugin.SpringBootPlugin::class.java)
        }
        project.pluginManager.withPlugin("java-library"){
            log2("has plugin java-library add SigningPlugin",project)
            project.pluginManager.apply(SigningPlugin::class.java)
        }
        project.pluginManager.withPlugin("maven-publish"){
            log2("has plugin maven-publish add MavenPublishNexusStagingPlugin",project)
            project.pluginManager.apply(name.remal.gradle_plugins.plugins.publish.nexus_staging.MavenPublishNexusStagingPlugin::class.java)
        }
    }

    private fun setRepositories(project: Project) {
        log2("setRepositories()",project)
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
}
