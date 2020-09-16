package com.github.jchanghong

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.plugins.signing.SigningExtension

internal fun end(project: Project, myExtension: JchPluginExtension) {
    project.pluginManager.withPlugin("com.gradle.plugin-publish") {
        val signingExtension = project.extensions.findByType(SigningExtension::class.java)
        project.tasks.withType(org.gradle.api.publish.maven.tasks.GenerateMavenPom::class.java).configureEach {
            if (it.pom == null) {
                it.doFirst {
                    val generateMavenPom = it as GenerateMavenPom
                    setMavenPOM(generateMavenPom.pom)

                }
                it.doLast {
//                    val name="generatePomFileForPluginMavenPublication"
                    val removeSuffix = it.name.removePrefix("generatePomFileFor").removeSuffix("Publication")
                    val publishingExtension =
                        project.extensions.findByType(PublishingExtension::class.java)?.publications
                            ?.firstOrNull { p -> removeSuffix == p.name.capitalize() }
                    if (signingExtension != null && publishingExtension != null) {
                        runCatching {
                            val sign = signingExtension.sign(publishingExtension)
                            sign.forEach {
                                project.logger.quiet("generate:" + it.name + it.filesToSign.first().absolutePath)
                                it.generate()
                            }
                            sign
                        }
                    }
                }
            }
        }
    }
}