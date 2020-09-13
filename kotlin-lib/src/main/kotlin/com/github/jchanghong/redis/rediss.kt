package com.github.jchanghong.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands

val json = """
    {
        "okNtp": 0,
        "channelNtpAt": "",
        "recordPercentage": 0,
        "okStatus": 0,
        "okRecord": 0,
        "recordLength": 0,
        "indexCode": "",
        "okQuality": 0,
        "recordLists": [
            {
                "length": 0,
                "hour": 0,
                "percentage": 0
            }
        ],
        "channelNtpImgUrl": "",
        "channelCapatureAt": ""
    }
""".trimIndent().replace("""\s+""".toRegex(), "")

fun main() {
    println(json)
    val redisHelper = RedisHelper("50.1.43.109")
    redisHelper.redisCommands.zadd("testzset", 1.0, "test")
    redisHelper.redisCommands.zadd("testzset", 3.0, "test3")
    redisHelper.redisCommands.zadd("testzset", 4.0, "test4")
    redisHelper.redisCommands.zadd("testzset", 2.0, "test2")
    redisHelper.redisCommands.zadd("testzset", 0.0, "test0")
    println(redisHelper.redisCommands.zpopmax("testzset"))
    redisHelper.close()
}

/** 一个对象对应一个redis服务器*/
class RedisHelper(ip: String, port: Int = 6379) {
    private val redisClient = RedisClient.create(RedisURI.create(ip, port))
    private val connect = redisClient.connect()
    val redisCommands: RedisCommands<String, String> = connect.sync()
    val asyncCommands: RedisAsyncCommands<String, String> = connect.async()
    fun close() {
        connect.close()
        redisClient.shutdown()
    }
}