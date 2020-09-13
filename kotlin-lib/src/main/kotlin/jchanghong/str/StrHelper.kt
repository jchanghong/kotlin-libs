package jchanghong.str

import cn.hutool.core.util.StrUtil

fun main() {
    println(teststr)
    println(teststr.length)
    println(StrUtil.removeAllLineBreaks(teststr).chunked(130).joinToString("\n"))
}

private val teststr = """
    sdasdasdasd
    select sasasas,
    dasdadasdasd,dasdasdasdasd,
    dasdasdasd,
    dasdadasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
""".trimIndent()

fun String.toCamelCase(): String {
    return StrUtil.toCamelCase(this)
}

fun String.toUnderlineCase(): String {
    return StrUtil.toUnderlineCase(this)
}

fun String.upperFirst(): String {
    return StrUtil.upperFirst(this)
}

object StrHelper {
    fun containsALLNocase(str: String, input: String): Boolean {
        var str = str
        var input = input
        str = str.toUpperCase()
        input = input.toUpperCase()
        val chars = str.toCharArray()
        val chars2 = input.toCharArray()
        var index = 0
        var count = 0
        for (i in chars2.indices) {
            while (index < chars.size) {
                if (chars2[i] == chars[index]) {
                    index++
                    count++
                    break
                }
                index++
            }
        }
        return count == chars2.size
    }
}
