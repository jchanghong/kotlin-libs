package com.github.jchanghong

import cn.hutool.core.io.resource.ResourceUtil
import org.gradle.api.Project
import org.gradle.plugins.signing.SigningExtension
val readUtf8Str = ResourceUtil.readUtf8Str("asc.asc")

/** 签名插件设置*/
internal fun setPropertie(project: Project) {
    project.pluginManager.withPlugin("signing") {
        project.extensions.findByType(SigningExtension::class.java)?.let {
            it.useInMemoryPgpKeys(readUtf8Str, "123buyaodaohao")
            log2("set signing key ", project)
        }
    }
}