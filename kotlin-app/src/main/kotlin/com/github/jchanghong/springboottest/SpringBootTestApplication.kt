package com.github.jchanghong.springboottest

import cn.hutool.json.JSONObject
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.github.jchanghong.autoconfig.AutoConfig
import com.github.jchanghong.http.OkHttps
import com.github.jchanghong.http.postJson
import com.github.jchanghong.springboottest.mapper1.auto.AutoMapperTbTest1
import com.github.jchanghong.springboottest.mapper1.auto.DOTbTest1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.Transactional

//@Repository
class Service {
    @Autowired
    lateinit var autoMapperTbTest1: AutoMapperTbTest1

    @Transactional(transactionManager = "a1_transactionManager")
    fun run(vararg args: String?) {
        AutoConfig.logSql = true
        for (map in autoMapperTbTest1.selectList(KtQueryWrapper(DOTbTest1::class.java).eq(DOTbTest1::name, 'a'))) {
            println(map)
        }
        val apply = DOTbTest1().apply {
            aint = 1
            name = "name"
            ajson = JSONObject().apply { set("a", "a") }
            ajsonb = JSONObject().apply { set("a", "a") }
        }
        autoMapperTbTest1.insert(apply)
        autoMapperTbTest1.mybatisInsertBatchPG(listOf(apply))
        if (args.isNullOrEmpty()) error("null")
        for (map in autoMapperTbTest1.selectList(null)) {
            println(map)
        }
        AutoConfig.logSql = true
        val selectList = autoMapperTbTest1.selectList(KtQueryWrapper(DOTbTest1::class.java).eq(DOTbTest1::name, "a"))
        println(selectList.size)
    }
}

@SpringBootApplication
@EnableElasticsearchRepositories
@EnableTransactionManagement(proxyTargetClass = true)
class SpringBootTestApplication : CommandLineRunner {

    //	@Autowired
    lateinit var service: Service

    //	@Autowired
    lateinit var autoMapperTbTest1: AutoMapperTbTest1
    override fun run(vararg args: String?) {
//		service.run(*args)
    }
}

fun main(args: Array<String>) {
//	AutoConfig.mybatisPlusConfigList.add(MybatisPlusConfig("a1",
//		DbUtil.getDs("db1"),"com.github.jchanghong.springboottest.mapper1"))
//	AutoConfig.mybatisPlusConfigList.add(MybatisPlusConfig("a2",
//		DbUtil.getDs("db2"),"com.github.jchanghong.springboottest.mapper2",
//		null))

    runApplication<SpringBootTestApplication>(*args)
}
