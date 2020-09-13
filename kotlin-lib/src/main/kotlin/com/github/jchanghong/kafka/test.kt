package com.github.jchanghong.kafka

import cn.hutool.json.JSONUtil
import com.github.jchanghong.log.kInfo
import java.util.function.Function

fun main() {

    for (i in (1..8)) {
        val kafkaHelper = KafkaHelper("50.1.43.110:9092", "check4297", listOf("camera_status_r2p16"), Function {
            val any = JSONUtil.parseObj(it.value().toString())["indexCode"].toString()
            kInfo(any)
        })
        kafkaHelper.startConsumer()
    }
}

