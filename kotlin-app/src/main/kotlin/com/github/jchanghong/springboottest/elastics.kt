package com.github.jchanghong.springboottest

import cn.hutool.core.util.RandomUtil
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Text
import jchanghong.http.OkHttps
import jchanghong.http.get
import jchanghong.http.postJson
import jchanghong.kotlin.toStrOrNow
import jchanghong.random.RandomHelper
import okhttp3.OkHttpClient
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Document(indexName = "test", type = "doc", shards = 10, replicas = 0)
data class Index1(@Id val id: Long? = 0, @Field(type = FieldType.Text) var name: String? = null) {
}
@Repository
interface El :ElasticsearchRepository<Index1, Long?>{}
@Service
class Elastics(val el: El) :CommandLineRunner{
    override fun run(vararg args: String?) {
        var a=1L;
        for (i in (1..10000)) {
            val map = (1..10000).map { Index1(a++, RandomHelper.randomWordList()) }
            el.saveAll(map)
        }
    }
}

@Configuration
class RestClientConfig : AbstractElasticsearchConfiguration() {
    @Bean
    override fun elasticsearchClient(): RestHighLevelClient {
        val client = RestHighLevelClient(
            RestClient.builder(
                HttpHost("localhost", 9200, "http"),
                HttpHost("localhost", 9201, "http")
            )
        )
        return client
    }
}

fun main() {
    val postJson = OkHttps.httpClient.postJson("http://127.0.0.1:9300/_sql?format=txt&pretty", """
        {
          "query": "SELECT * FROM test where name like 'abc%' LIMIT 5"
        }
    """.trimIndent())
    println(postJson)
}