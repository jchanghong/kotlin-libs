package com.github.jchanghong.springboottest.mapper1

import com.github.jchanghong.springboottest.mapper1.auto.DOTbTest1
import org.apache.ibatis.annotations.Select

interface Mapper1 {
    @Select("select * from tb_test1")
    fun list():List<DOTbTest1>
}
interface Mapper2 {
    @Select("select * from check_org")
    fun list():List<Map<String,Any?>>
}