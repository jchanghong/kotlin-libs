package com.github.jchanghong.autoconfig

import cn.hutool.core.io.resource.ResourceUtil
import com.github.jchanghong.autoconfig.db.mybatis.JBeanNameGenerator
import com.github.jchanghong.autoconfig.db.mybatis.MyBatisPlugin
import com.github.jchanghong.database.DBHelper
import com.github.jchanghong.kotlin.isNotNUllOrBlank2
import com.github.jchanghong.log.kInfo
import org.apache.ibatis.jdbc.ScriptRunner
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.function.Supplier
import javax.annotation.PostConstruct
import javax.sql.DataSource

/** 库全局配置*/
object AutoConfig {
    var logSql = false
    var lockDB = false
    var swagger2BasePackage: String? = null
    val mybatisPlusConfigList = arrayListOf<MybatisPlusConfig>()
}

/** spring boot 自动配置mybatis plus。多数据库，在spring 容器启动前加入配置！！！*/
data class MybatisPlusConfig @JvmOverloads constructor(
        val beanName: String, val dataSource: DataSource,
        val mapperInterfacePackage: String,
        val mapperXMLLocations: String? = null,
        /** 初始化sql,class path 路径文件*/
        val initSql: String? = null
)

@EnableConfigurationProperties(SqlAutoConfigurationProperties::class)
@Import(
        value = [MyBatisPlugin::class, SwaggerConfig::class
        ]
)
@AutoConfigureOrder(Int.MAX_VALUE)
open class SqlAutoConfiguration : BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        kInfo("setApplicationContext${applicationContext.beanDefinitionCount}")
    }

    //    步骤3
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        if (beanFactory is DefaultListableBeanFactory) {
            kInfo("postProcessBeanFactory${beanFactory.beanDefinitionCount}")
            if (AutoConfig.mybatisPlusConfigList.size > 0) {
                for (mybatisPlusConfig in AutoConfig.mybatisPlusConfigList) {
                    if (mybatisPlusConfig.initSql.isNotNUllOrBlank2()) {
                        kInfo("执行init sql文件 ${mybatisPlusConfig.initSql}")
                        val sql = ResourceUtil.readUtf8Str(mybatisPlusConfig.initSql)
                        val sqlSessionFactory =
                                beanFactory.getBean(mybatisPlusConfig.beanName, SqlSessionFactory::class.java)
                        sqlSessionFactory.openSession().use {
                            val scriptRunner = ScriptRunner(it.connection)
                            scriptRunner.setStopOnError(true)
                            scriptRunner.runScript(ResourceUtil.getUtf8Reader(mybatisPlusConfig.initSql))
                        }
                    }
                }
            }
        }
    }

    // 步骤2
    @OptIn(ExperimentalStdlibApi::class)
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        kInfo("postProcessBeanDefinitionRegistry${registry.beanDefinitionCount}")
        if (AutoConfig.mybatisPlusConfigList.size > 0) {
            kInfo("配置 AutoConfig.mybatisPlusConfigList ${AutoConfig.mybatisPlusConfigList.size}")
            for ((index, mybatisPlusConfig) in AutoConfig.mybatisPlusConfigList.withIndex()) {
                val beanName = mybatisPlusConfig.beanName
                registry.registerBeanDefinition(beanName + "_sqlSessionFactory", GenericBeanDefinition().apply {
                    instanceSupplier = Supplier {
                        DBHelper.getMybatisSqlSessionFactory(
                                mybatisPlusConfig.dataSource,
                                mybatisPlusConfig.mapperInterfacePackage, mybatisPlusConfig.mapperXMLLocations
                        )
                    }
                    this.isPrimary = index == 0
                })
                registry.registerBeanDefinition(beanName + "_transactionManager",
                        GenericBeanDefinition().apply {
                            instanceSupplier = Supplier {
                                DataSourceTransactionManager(mybatisPlusConfig.dataSource)
                            }
                            this.isPrimary = index == 0
                        })
                DBHelper.newMapperScannerConfigurer(
                        mybatisPlusConfig.mapperInterfacePackage,
                        beanName + "_sqlSessionFactory"
                ).apply {
                    nameGenerator = JBeanNameGenerator()
                    postProcessBeanDefinitionRegistry(registry)
                }
            }
        }
        kInfo("postProcessBeanDefinitionRegistry${registry.beanDefinitionCount}")
    }


    @PostConstruct
    fun init() {
        kInfo("jchanghong自动配置完成==============================")
    }
}

@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select() // 自行修改为自己的包路径
                .apis(RequestHandlerSelectors.basePackage(AutoConfig.swagger2BasePackage.toString()))
                .paths(PathSelectors.any())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("接口文档")
                .description("接口文档") //服务条款网址
                //.termsOfServiceUrl("http://blog.csdn.net/forezp")
                .version("1.0") //.contact(new Contact("岳阳", "url", "email"))
                .build()
    }
}