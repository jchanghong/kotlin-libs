package com.github.jchanghong.file

import cn.hutool.core.io.FileUtil
import com.github.jchanghong.log.kInfo
import java.io.File

/**
 *
 * @author : jiangchanghong
 *
 * @version : 2020-01-08 16:46
 **/
object FileHelper {
    fun copyFiles(root: String, destPath: String, vararg pathRegex: String) {
        val map = pathRegex.map { it.toRegex() }
        val desp = File(destPath)
        if (!FileUtil.isDirectory(root)) {
            error("$root 不是目录")
        }
        File(root).walkBottomUp().forEach {
            if (it.isFile) {
                val firstOrNull =
                        map.firstOrNull { regex -> !(regex.find(it.absolutePath)?.groupValues.isNullOrEmpty()) }
                if (firstOrNull != null) {
                    val removePrefix = it.absolutePath.removePrefix(root).removePrefix("/").removePrefix("\\")
                    val target = File(desp, removePrefix)

                    println(it.absolutePath + "-> " + target.absolutePath)
                    it.copyTo(target, true)
                }
            }
        }
    }

    private fun File.myDelete(): Unit {
        if (this.isDirectory) return
        val name = this.name
        if (name.endsWith(".lastUpdated ") || name.endsWith("_remote.repositories")) {
            this.delete()
            kInfo(this.absolutePath.toString() + "已删除")
        }
    }

    @JvmOverloads
            /**删除文件   .lastUpdated _remote.repositories */
    fun removeMavenLastAndRemoteFiles(path: String, group: String = ""): Unit {
        var parent = File(path)
        if (!parent.exists() || parent.isFile) {
            error(path + "目录不存在")
        }
        if (group.isNotEmpty()) {
            parent = File(parent, group.split(".").joinToString(separator = "/"))
            if (!parent.exists() || parent.isFile) {
                error(path + "目录不存在")
            }
        }
        parent.walkTopDown().forEach { it.myDelete() }
    }
}


fun main() {
//    .lastUpdated _remote.repositories
//    FileHelper.removeMavenLastAndRemoteFiles("""D:\mavendir""")

    FileHelper.copyFiles(
            "D:\\mavendir",
            """D:\acopymavendir""",
            """hikvision""",
            """apollo""",
            """sun""",
            """jef""",
            """nonpoplar""",
            """hik"""
    )
}
