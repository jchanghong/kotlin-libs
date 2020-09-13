package jchanghong.autoconfig

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jch")
class SqlAutoConfigurationProperties {
    /**mybatis和hutool显示sql*/
    var logSql = true

    /** 禁止更新数据库*/
    var lockDB = false
}
