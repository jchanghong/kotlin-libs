package com.github.jchanghong.elasticsearch

import cn.hutool.json.JSONUtil
import com.github.jchanghong.gson.jsonByPath
import com.github.jchanghong.gson.jsonToObject
import com.github.jchanghong.gson.toJsonStr
import com.github.jchanghong.http.OkHttps
import com.github.jchanghong.http.get
import com.github.jchanghong.http.postJson

object ElasticsearchHelper{
    /** http://127.0.0.1:9200*/
    var serverUrl="http://127.0.0.1:9200"
    fun showTables(): String {
        val postJson = OkHttps.httpClient.postJson("${serverUrl}/_sql?format=txt&pretty", """
        {
          "query": "show tables"
        }
    """.trimIndent())
        return postJson
    }
    fun DESCRIBE(table :String): String {
        val postJson = OkHttps.httpClient.postJson("${serverUrl}/_sql?format=txt&pretty", """
        {
          "query": "DESCRIBE $table"
        }
    """.trimIndent())
        return postJson
    }
    fun queryForJson(sql:String): String {
        val postJson = OkHttps.httpClient.postJson("${serverUrl}/_sql?format=json&pretty", """
        {
          "query": "$sql"
        }
    """.trimIndent())
//        println(postJson)
        val jsonToObject = postJson.jsonToObject<Map<String, List<Any>>>()
        val keys = (jsonToObject?.get("columns") as? List<Map<String, String>>?)?.mapNotNull { it["name"] }
        val list = jsonToObject?.get("rows")?.mapNotNull { it as? List<Any?> }
                ?.mapNotNull {
                    val toMap = keys?.mapIndexed { index, s -> s to it[index] }?.toMap()
                    toMap
                }
        val toString = list.toJsonStr().toString()
        val message = JSONUtil.formatJsonStr(toString)
//        println(message)
        return message
    }
    fun queryForTxt(sql:String): String {
        val postJson = OkHttps.httpClient.postJson("${serverUrl}/_sql?format=txt&pretty", """
        {
          "query": "$sql"
        }
    """.trimIndent())
        return postJson
    }
    fun index(table:String,id:Long,data:String): String {
        val postJson = OkHttps.httpClient.postJson("${serverUrl}/${table}/_doc/${id}", data)
        return postJson
    }
    @JvmOverloads
    fun query(field:String,query:String,table:String?=null): String {
        val body="""
            {

              "query": {

                "match": { "$field": "$query" }

              },

              "highlight": {

                "fields": {

                  "$field": {}

                }

              }

            }
        """.trimIndent()
        val s = if (table!=null)"${table}/" else ""
        val postJson = OkHttps.httpClient.postJson("${serverUrl}/${s}_search?pretty", body)
        val jsonToObject = postJson.jsonByPath("hits.hits").jsonToObject<List<Map<String, Any?>>>()
        val mapNotNull = jsonToObject?.mapNotNull { val any = it["_source"] as? MutableMap<String,Any?>?
            if (any != null) {
                val get = (it.get("highlight") as? Map<String, Any?>)?.get(field)
                any["highlight"]= (get as? List<String>?)?.get(0)
            }
            any
        }
        val toString = mapNotNull.toJsonStr().toString()
        return JSONUtil.formatJsonStr(toString)
    }
}