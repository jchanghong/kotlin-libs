package com.github.jchanghong.springboottest

import cn.hutool.db.DbUtil
import jchanghong.database.TableHelper

class AppTest

fun main() {
   TableHelper.toFile(DbUtil.getDs("db1"),"tb_test1",
       "com.github.jchanghong.springboottest.mapper1","spring-boot-test")
}

