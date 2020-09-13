package jchanghong.gson

import cn.hutool.json.JSONUtil

object JSONHelper2 {

    fun jsonToKotlin(json: String): String {
        val parseObj = JSONUtil.parseObj(json)
        val joinToString = parseObj.entries.map { (k, v) ->
            val type = when {
                v::class == Int::class || v::class == Long::class -> "Long"
                v::class == String::class -> "String"
                else -> "String"
            }
            "var $k :$type ? =null"
        }.joinToString(separator = ",\n")

        return """
       data class C1($joinToString) 
    """.trimIndent()
    }
}

fun main() {
    val json = """
         {
        "devType": 1,
        "collectTime": 1598605222000,
        "msgType": 0,
        "devNo": "100086",
        "latitude": 0,
        "version": "1.0",
        "voltageLow": 0,
        "linkageDevCode": "",
        "siteNo": "",
        "vendor": "HIKVISION",
        "rfidIdentifier": "01500234024416",
        "longitude": 0
    }
    """.trimIndent()

    println(JSONHelper2.jsonToKotlin(json))
}

data class test2(var s: String)