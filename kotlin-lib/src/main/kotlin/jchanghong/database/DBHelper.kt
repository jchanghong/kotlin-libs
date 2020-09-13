package jchanghong.database

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.text.csv.CsvReadConfig
import cn.hutool.core.text.csv.CsvRow
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.core.text.csv.CsvWriteConfig
import cn.hutool.core.util.ClassUtil
import cn.hutool.db.DbUtil
import cn.hutool.db.ds.DataSourceWrapper
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
import com.zaxxer.hikari.pool.HikariProxyConnection
import jchanghong.autoconfig.db.mybatis.MybatisPlusConfig
import jchanghong.io.IOHelper
import jchanghong.kotlin.log
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.session.ExecutorType
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.mapper.MapperScannerConfigurer
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import org.postgresql.jdbc.PgConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.sql.Connection
import javax.sql.DataSource
import kotlin.streams.toList

fun main() {
    val array = ClassUtil.scanPackageBySuper("jchanghong.autoconfig.db.mybatis", Interceptor::class.java)
        .map { it.newInstance() as? Interceptor }.toTypedArray()
    val db = DbUtil.use()
    val sqlSessionTemplate: SqlSessionTemplate? = null;
    val list = sqlSessionTemplate!!.selectList<String>("")
//    sqlSessionTemplate.
}

fun DataSource?.pgConnection(): PgConnection? {
    var dataSource = this
    if (dataSource is DataSourceWrapper) {
        dataSource = dataSource.raw
    }
    var connection = dataSource?.connection
    if (connection is HikariProxyConnection) {
        connection = BeanUtil.getFieldValue(connection, "delegate") as Connection?
    }
    return connection as? PgConnection
}

object DBHelper {


    /** 返回数据源的key，一般是url*/
    fun dsKey(ds: DataSource): String {
        val firstOrNull =
            BeanUtil.getPropertyDescriptorMap(ds::class.java, true).entries?.firstOrNull {
                it.key.contains(
                    "url",
                    true
                )
            }
        val invoke = firstOrNull?.value?.readMethod?.invoke(ds).toString()
        log.info(invoke)
        return ds::class.qualifiedName.toString() + ds.toString() + invoke
    }

    @OptIn(ExperimentalStdlibApi::class)
    @JvmOverloads
    fun pgCopyTo(ds: DataSource, tableName: String, maxBuffer: Int = 1024 * 1024 * 1024): String {
        val copyManager = CopyManager(ds.pgConnection() as BaseConnection)
        val byteArrayOutputStream = ByteArrayOutputStream(maxBuffer)
        copyManager.copyOut("COPY $tableName to stdout with csv header", byteArrayOutputStream)
        val string = byteArrayOutputStream.toByteArray().decodeToString()
        return string
    }

    @JvmOverloads
    fun pgCopyFrom(ds: DataSource, tableName: String, data: String): Long {
        val copyManager = CopyManager(ds.pgConnection() as BaseConnection)
        val copyIn = copyManager.copyIn("COPY $tableName from stdin with csv header", data.reader())
        return copyIn
    }

    private val interceptPackages = "jchanghong.autoconfig.db.mybatis"
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    //    datasource to mapper 集合
    private val mybatisTemplateMap = HashMap<String, SqlSessionTemplate>()
    private val mybatisFatoryMap = HashMap<String, SqlSessionFactory>()
    const val DRIVER_CLASS_ORACLE = "oracle.jdbc.driver.OracleDriver"
    const val DRIVER_CLASS_POSTGRES = "org.postgresql.Driver"

    /** 包名可以为null scan mapper*/
    @JvmOverloads
    @JvmStatic
    fun getMybatisSqlSessionTemplate(
        dataSource: DataSource, mapperInterfacePackage: String? = null,
        mapperXMLLocations: String? = null, executorType: ExecutorType = ExecutorType.SIMPLE
    ): SqlSessionTemplate {
        val key = dsKey(dataSource)
        val sqlSessionTemplate = mybatisTemplateMap[key]
        if (sqlSessionTemplate != null) return sqlSessionTemplate
        val template = SqlSessionTemplate(
            getMybatisSqlSessionFactory(dataSource, mapperInterfacePackage, mapperXMLLocations),
            executorType
        )
        mybatisTemplateMap[key] = template
        return template
    }

    /** 包名可以为null scan mapper*/
    @JvmOverloads
    @JvmStatic
    fun getMybatisSqlSessionFactory(
        dataSource: DataSource, mapperInterfacePackage: String? = null,
        mapperXMLLocations: String? = null
    ): SqlSessionFactory {
        val key = dsKey(dataSource)
        val sessionFactory = mybatisFatoryMap[key]
        if (sessionFactory != null) return sessionFactory
        logger.info("开始建立mybatisSqlSessionFactory")
        // TODO 使用 MybatisSqlSessionFactoryBean 而不是 SqlSessionFactoryBean
        val factory = MybatisSqlSessionFactoryBean()
        val configuration = MybatisConfiguration()
        factory.configuration = configuration
        factory.setDataSource(dataSource)
        factory.vfs = SpringBootVFS::class.java
        val list = ClassUtil.scanPackageBySuper(interceptPackages, Interceptor::class.java)
            .mapNotNull { it.newInstance() as? Interceptor }
        factory.setPlugins(
            *list.toTypedArray(), MybatisPlusConfig.paginationInterceptor()
        )
//        if (StringUtils.hasLength(typeAliasesPackage)) {
//            factory.setTypeAliasesPackage(typeAliasesPackage)
//        }
        factory.setTypeHandlersPackage(interceptPackages)

        if (!mapperXMLLocations.isNullOrBlank()) {
            IOHelper.resolveMapperLocations(mapperXMLLocations)?.let {
                factory.setMapperLocations(*it)
            }
        }
        // TODO 此处必为非 NULL
        val globalConfig = GlobalConfigUtils.defaults()
        // TODO 注入填充器
//        globalConfig.metaObjectHandler = null

        // TODO 注入主键生成器
//        globalConfig.dbConfig.keyGenerator = null

        // TODO 注入sql注入器
//        globalConfig.sqlInjector = null

        // TODO 注入ID生成器
//        globalConfig.identifierGenerator = null
        // TODO 设置 GlobalConfig 到 MybatisSqlSessionFactoryBean
        factory.setGlobalConfig(globalConfig)
        if (!mapperInterfacePackage.isNullOrBlank()) {
            configuration.mapperRegistry.addMappers(mapperInterfacePackage)
        }
        val sqlSessionFactory = factory.getObject()
        mybatisFatoryMap[key] = sqlSessionFactory!!
        logger.info("建立 mybatisSqlSessionFactory完成 $dataSource")
        return sqlSessionFactory
    }

    @JvmOverloads
    fun <T> getMybatisMapper(
        type: Class<T>,
        dataSource: DataSource,
        executorType: ExecutorType = ExecutorType.SIMPLE
    ): T {
        val sessionTemplate = getMybatisSqlSessionTemplate(dataSource, executorType = executorType)
        sessionTemplate.configuration.mapperRegistry
            .addMapper(type)
        return sessionTemplate.getMapper(type)
    }

    @JvmStatic
    fun newMapperScannerConfigurer(basePackage: String, SqlSessionFactoryBeanName: String): MapperScannerConfigurer {
        val configurer = MapperScannerConfigurer()
        configurer.setBasePackage(basePackage)
        configurer.setSqlSessionFactoryBeanName(SqlSessionFactoryBeanName)
        return configurer
    }

    fun <T : Any> csvToBeanList(csv: String, clazz: Class<T>): List<T> {
        val list = mutableListOf<CsvRow>()
        CsvUtil.getReader(CsvReadConfig.defaultConfig().apply {
            this.setContainsHeader(true)
        })
            .read(csv.reader()) { row ->
                list.add(row)
            }
        return list.parallelStream().map {
            val mapToBean = BeanUtil.mapToBean(it.fieldMap, clazz, true)
            mapToBean
        }.toList()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun <T : Any> beanListToCsv(list: List<T>): String {
        val one = list.firstOrNull() ?: return ""
        val map = BeanUtil.beanToMap(one, true, false)
        val byteArrayOutputStream = ByteArrayOutputStream(1000000)
        val readerUTF8 = byteArrayOutputStream.writer(Charsets.UTF_8)
        val csvWriter = CsvUtil.getWriter(readerUTF8, CsvWriteConfig.defaultConfig().apply {})
        csvWriter.write(map.keys.toTypedArray())
        for (t in list) {
            val beanMap: MutableMap<String, Any?> = BeanUtil.beanToMap(t, true, false)
            val typedArray = beanMap.values.map { it?.toString() }.toList().toTypedArray()
            csvWriter.write(typedArray)
        }
        return byteArrayOutputStream.toByteArray().decodeToString()
    }
}
