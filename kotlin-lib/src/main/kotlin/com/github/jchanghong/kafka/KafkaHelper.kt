package com.github.jchanghong.kafka

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.RandomUtil
import com.github.jchanghong.kotlin.toStrOrNow
import com.github.jchanghong.log.kError
import com.github.jchanghong.log.kInfo
import org.apache.kafka.clients.admin.*
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.KafkaFuture
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.function.Function

/** 一个对象一套kafka配置，多个kafka，需要建立多个对象,earliest,latest*/
class KafkaHelper(
        val bootstrap: String,
        val groupId: String,
        val topics: List<String>? = null,
        val action: Function<ConsumerRecord<String?, String?>, Unit>? = null,
        val offsetReset: String = "latest"
) {
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()
    private val mProps: Properties by lazy { getAndSetProps(bootstrap, groupId) }
    private val mProducer: KafkaProducer<String, String> by lazy { KafkaProducer<String, String>(mProps) }
    private val mConsumer: KafkaConsumer<String, String> by lazy { KafkaConsumer<String, String>(mProps) }
    private val adminClient: AdminClient by lazy { KafkaAdminClient.create(mProps) }

    // 配置Kafka
    private fun getAndSetProps(bootstrap: String, groupId: String? = null): Properties {
        val props = Properties()
        props["bootstrap.servers"] = bootstrap
//        props.put("retries", 2) // 重试次数
        props.put("batch.size", 16384) // 批量发送大小
//        props.put("buffer.memory", 33554432) // 缓存大小，根据本机内存大小配置
//        props.put("linger.ms", 1000) // 发送频率，满足任务一个条件发送

        props.put("acks", "1")
        if (!groupId.isNullOrBlank()) {
            props.setProperty("group.id", groupId)
        }
        props.setProperty("enable.auto.commit", "true")
        props.setProperty("auto.offset.reset", offsetReset)
        props.setProperty("auto.commit.interval.ms", "1000")
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        return props
    }

    @JvmOverloads
    fun createTopic(name: String, p: Int = 8, r: Short = 1) {
        val newTopic = NewTopic(name, p, r)
        val newTopicList: MutableCollection<NewTopic> = ArrayList()
        newTopicList.add(newTopic)
        val createTopicsResult = adminClient.createTopics(newTopicList)
        for (entry in createTopicsResult.values()) {
            try {
                entry.value.get()
                Thread.sleep(2000)
            } catch (e: Exception) {
                kError(e.message, e)
            }
            kInfo("createTopic ${entry.key}")
        }
    }

    fun deleteTopic(name: String) {
        val deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(name))
        for ((k, v) in deleteTopicsResult.values()) {
            try {
                v.get()
                Thread.sleep(2000)
            } catch (e: Exception) {
                kError(e.message, e)
            }
            kInfo("deleteTopic $k")
        }
    }

    fun listAllTopic(): Set<String> {
        val result: ListTopicsResult = adminClient.listTopics()
        val names = result.names()
        try {
            return names.get()
        } catch (e: InterruptedException) {
            kError(e.message, e)
        } catch (e: ExecutionException) {
            kError(e.message, e)
        }
        return emptySet()
    }

    fun getTopic(name: String): TopicDescription? {
        val describeTopics: DescribeTopicsResult = adminClient.describeTopics(Arrays.asList(name))
        val values: Collection<KafkaFuture<TopicDescription>> = describeTopics.values().values
        if (values.isEmpty()) {
            kInfo("找不到描述信息")
        } else {
            for (value in values) {
                return value.get()
            }
        }
        return null
    }

    fun produce(topic: String, value: String, key: String? = null) {
        mProducer.send(ProducerRecord(topic, key ?: "${System.nanoTime()}${RandomUtil.randomString(20)}", value))
    }

    fun startConsumer() {
        checkNotNull(topics)
        checkNotNull(action)
        val method = ClassUtil.getDeclaredMethod(KafkaConsumer::class.java, "subscribe", List::class.java)
        val pollMethod = ClassUtil.getDeclaredMethod(KafkaConsumer::class.java, "poll", Duration::class.java)
        if (method != null) {
//            老版本版本kafka
            method.invoke(mConsumer, topics)
//            mConsumer.subscribe(topics)
            singleThreadExecutor.execute {
                while (true) {
                    val poll = if (pollMethod != null) mConsumer.poll(Duration.ofMillis(100)) else mConsumer.poll(0)
                    val records: ConsumerRecords<String, String> = poll ?: continue
                    records.forEach {
                        action.apply(it)
                    }
                }
            }
        }
    }
}


fun main() {

//    kafkaHelper.deleteTopic("testr2p8")
//    kafkaHelper.createTopic("camera_status_r2p16",16,2)
//    kafkaHelper.createTopic("camera_tag_r2p16",16,2)
    val kafkaHelper = KafkaHelper("50.1.43.110:9092", "group3", listOf("testr2p8"), Function {
        kInfo(
                it.value()
                        .toString() + " group1 consumer1 ${it.partition()} ${it.offset()}  ${it.key()}  ${Date(it.timestamp()).toStrOrNow()}"
        )
    })
    for (i in (1..8)) {
        val kafkaHelper = KafkaHelper("50.1.43.110:9092", "group3", listOf("testr2p8"), Function {
            kInfo(
                    it.value()
                            .toString() + " group1 consumer1 ${it.partition()} ${it.offset()}  ${it.key()}  ${Date(it.timestamp()).toStrOrNow()}"
            )
        })
        kafkaHelper.startConsumer()
    }
    ThreadUtil.sleep(8000)
    (1..10).toList().forEach {
        kafkaHelper.produce("testr2p8", "1gentest${it}" + DateUtil.now())
    }
//    println("end1")
}