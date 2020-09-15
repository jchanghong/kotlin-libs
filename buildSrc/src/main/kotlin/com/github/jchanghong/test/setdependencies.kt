package com.github.jchanghong.test

import cn.hutool.core.util.StrUtil
import org.gradle.api.Project

/** 第一个是group map，第2个是 完整的group:name map*/
fun versions(map: Map<String,String>): Pair<Map<String, String>, Map<String, String>> {
    val groupmap = map.filterKeys { it.indexOf(":") < 0 }
    val groupmap2 = map.filterKeys { it.indexOf(":") > 0 }
    return groupmap to groupmap2
}
internal fun setdependencies(project: Project, myExtension: JchPluginExtension) {
    log2("setdependencies()", project)
    val (gmap,nmap) = versions(myExtension.mavenVersionMap)
    val allDepens = project.configurations.filter {
        it.name == "api" || it.name == "implementation" || it.name == "compile" || it.name == "testApi" || it.name == "testImplementation"
    }.flatMap { conf ->
        conf.dependencies.map { "${it.group}:${it.name}" }
    }.toHashSet()
    addtestImplementation("org.springframework.boot:spring-boot-starter-test", "2.3.3.RELEASE", project, allDepens)
    addtestImplementation("org.jetbrains.kotlin:kotlin-test-junit", "1.4.10", project, allDepens)
    project.configurations.all {
        it.resolutionStrategy.eachDependency {
            if (it.requested.group == "org.jetbrains.kotlin") {
                it.useVersion("1.4.10")
            }
            if (it.requested.group == "com.squareup.okhttp3" && it.requested.name == "okhttp") {
                it.useVersion("4.9.0")
            }
            if (gmap.containsKey(it.requested.group)) {
                it.useVersion(gmap[it.requested.group] ?: error(""))
            }
            val key = "${it.requested.group}:${it.requested.name}"
            if (nmap.containsKey(key)) {
                it.useVersion(nmap[key] ?: error(""))
            }
        }
    }
}

internal fun addtestImplementation(key: String, version: String, project: Project, allDepens: HashSet<String>) {
    if (key !in allDepens) {
        project.dependencies.add("testImplementation", "${key}:${version}")
    }
}

internal fun addImplementation(key: String, version: String, project: Project, allDepens: HashSet<String>) {
    if (key !in allDepens) {
        project.dependencies.add("implementation", "${key}:${version}")
    }
}