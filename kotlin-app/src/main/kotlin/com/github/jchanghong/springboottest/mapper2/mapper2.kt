package com.github.jchanghong.springboottest.mapper2

import org.apache.ibatis.annotations.Select

interface Mapper1 {
    @Select("select * from check_org")
    fun list():List<Map<String,Any?>>
}
interface Mapper2 {
    @Select("select * from check_org")
    fun list():List<Map<String,Any?>>
}