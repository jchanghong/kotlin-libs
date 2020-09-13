package jchanghong.lang

import cn.hutool.core.exceptions.ExceptionUtil

/**
 *
 * @author : jiangchanghong
 *
 * @version : 2020-03-03 17:22
 **/
fun Throwable.printStr(containline: String): String {
    val list = ExceptionUtil.stacktraceToString(this).split("""\s+""".toRegex())
    val message = list.filter { it.contains(containline) }
    return """
        |${ExceptionUtil.getMessage(this)}
        |${message.joinToString(separator = "\n")}
    """.trimMargin()
}
