package com.github.jchanghong.springboottest

import com.github.jchanghong.elasticsearch.ElasticsearchHelper
import com.github.jchanghong.http.OkHttps
import com.github.jchanghong.http.postJson
import com.github.jchanghong.random.RandomHelper
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
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
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong


@Document(indexName = "test", type = "doc", shards = 10, replicas = 0)
data class Index1(@Id val id: Long? = 0, @Field(type = FieldType.Text) var name: String? = null) {
}

@Repository
interface El : ElasticsearchRepository<Index1, Long?> {}

@Service
class Elastics(val el: El) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val newFixedThreadPool = Executors.newFixedThreadPool(16)
        var a = AtomicLong(30000000)
        (1..10000).map { newFixedThreadPool.submit {
            val map = (1..10000).map { Index1(a.incrementAndGet(), RandomHelper.randomWordList()) }
            el.saveAll(map)
        }.get()
        } }

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
    println(ElasticsearchHelper.query("name","this is abcd asa d"))
    println(ElasticsearchHelper.index("test2", 1, """
        {
        "_class":"com.github.jchanghong.springboottest.Index1",
        "id":6422599,
        "name":"gwlqnbk owpwmgxiv xmn oewi loa zzx ahpgi nygbkdabt cdhacro hzouib ryk qfbs fjovklt axfnqiz rnx huawyk xuz wexcvqath teyunal qhhjxs fcmpvyn ffumdmx qzgptcnb azblkjhga eolwo pnmbvicqj vzwrwree fyrt yjzfy vnfvyjsja dvvdkml iwhon ksswjz paj tvazzczz"
    }
    """.trimIndent()))
    println(ElasticsearchHelper.showTables())
    println(ElasticsearchHelper.DESCRIBE("test"))
    val sql = "select * from test where name like '%lkjhga%' limit 2"
    println(ElasticsearchHelper.queryForTxt(sql))
    println(ElasticsearchHelper.queryForJson(sql))
}