/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.github.jchanghong.test

import org.gradle.api.Plugin
import org.gradle.api.Project

internal fun log2(log: Any?, project: Project, logInfo: Boolean) {
    val get = project.properties.get("jch.debug").toString().toBoolean()
    if (logInfo) {
        project.logger.quiet("set project【${project.name}】:${log.toString()}")
    }
    else{
        if (get) {
            project.logger.quiet("set project【${project.name}】:${log.toString()}")
        }
    }
}

open class JchPluginExtension {
    companion object {
        /** org.springframework.boot:spring-boot-starter-*/
        const val springBootDependencies = "org.springframework.boot:spring-boot-starter-"
    }

    var logInfo = false
    var springBootversion = "2.3.3.RELEASE"
    var kotlinVersion = "1.4.10"
    var okhttpVersion = "4.9.0"

    /** 强制设置版本号，key是 group:name,或者group*/
    var mavenVersionMap: MutableMap<String, String> = mutableMapOf()

    /** org.springframework.boot:spring-boot-starter-{}*/
    var springBootDependencies = arrayListOf("web")
    var message = "1Hello from GreetingPlugin"
}

/**
 * A simple 'hello world' plugin.
 * 一个工程一个对象实例
 */
class JchGradlePlugin : Plugin<Project> {
    lateinit var myExtension: JchPluginExtension
    override fun apply(project: Project) {
        myExtension = project.extensions.create("jch", JchPluginExtension::class.java)
        setPropertie(project, myExtension)
        setRepositories(project, myExtension)
        addPlugin(project, myExtension)
        project.afterEvaluate { afterEvaluate(it) }
    }

    private fun afterEvaluate(project: Project) {
        log2("afterEvaluate()", project, myExtension.logInfo)
        configurationPlugin(project, myExtension)
        setdependencies(project, myExtension)
        addMyTasks(project, myExtension)
        end(project, myExtension)
    }


}
