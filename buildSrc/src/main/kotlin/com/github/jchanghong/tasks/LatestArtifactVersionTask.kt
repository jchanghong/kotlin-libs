package com.github.jchanghong.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File


open class LatestArtifactVersionTask : DefaultTask() {
    @get:Input
    var coordinates: String? = null

    @get:Input
    var serverUrl: String? = null

    @get:OutputDirectory
    var outPut:File?=null
    init {
        outPut=this.project.projectDir
    }
    @TaskAction
    fun resolveLatestVersion() {
        println("Retrieving artifact $coordinates from $serverUrl")
        // issue HTTP call and parse response
    }
}