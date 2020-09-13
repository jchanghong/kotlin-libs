package com.github.jchanghong.springboottest.mapper1.auto
import com.baomidou.mybatisplus.annotation.*
import com.github.liaochong.myexcel.core.annotation.ExcelColumn
import com.github.liaochong.myexcel.core.annotation.ExcelTable
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.annotations.Update
import org.springframework.stereotype.Service
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import cn.hutool.json.JSONObject
import com.github.liaochong.myexcel.core.annotation.ExcelModel
import jchanghong.autoconfig.db.mybatis.PGJsonTypeHandler

/**

* @Date 2020-08-08
* 此文件为自动生成，请勿修改！！！
* @User jiangchanghong
*/
@ExcelModel(includeAllField = true,  excludeParent = false,titleSeparator = "->",useFieldNameAsTitle = true,  wrapText = true)
@TableName(value = "tb_test1", schema = "", keepGlobalPrefix = false, resultMap = "", autoResultMap = true)
data class DOTbTest1 @JvmOverloads constructor(
    @ExcelColumn(title = "id",order = 0,  defaultValue = "0", convertToString = false,format = "",mapping = "", width = -1)
    @TableId(value = "id", type = IdType.AUTO)
    var  id : Long ? = null,
    @ExcelColumn(title = "name",order = 1,  defaultValue = "", convertToString = false,format = "",mapping = "", width = -1)
    @TableField(value = "name",exist = true, numericScale = "" )
    var  name : String ? = null,
    @ExcelColumn(title = "info",order = 2,  defaultValue = "", convertToString = false,format = "",mapping = "", width = -1)
    @TableField(value = "info",exist = true, numericScale = "" )
    var  info : String ? = null,
    @ExcelColumn(title = "aint",order = 3,  defaultValue = "0", convertToString = false,format = "",mapping = "", width = -1)
    @TableField(value = "aint",exist = true, numericScale = "" )
    var  aint : Long ? = null,
    @ExcelColumn(title = "ajsonb",order = 4,  defaultValue = "", convertToString = false,format = "",mapping = "", width = -1)
    @TableField(value = "ajsonb",exist = true, numericScale = "" ,jdbcType = JdbcType.OTHER,typeHandler = PGJsonTypeHandler::class)
    var  ajsonb : JSONObject ? = null,
    @ExcelColumn(title = "ajson",order = 5,  defaultValue = "", convertToString = false,format = "",mapping = "", width = -1)
    @TableField(value = "ajson",exist = true, numericScale = "" ,jdbcType = JdbcType.OTHER,typeHandler = PGJsonTypeHandler::class)
    var  ajson : JSONObject ? = null
)
interface AutoMapperTbTest1 : BaseMapper<DOTbTest1>{
    @Update("""
                   <script>
INSERT INTO tb_test1 ( name,info,aint,ajsonb,ajson
   )
   VALUES
   <foreach collection="list" separator="," item="a">
   ( #{a.name  },#{a.info  },#{a.aint  },#{a.ajsonb ,jdbcType = OTHER,typeHandler = jchanghong.autoconfig.db.mybatis.PGJsonTypeHandler },#{a.ajson ,jdbcType = OTHER,typeHandler = jchanghong.autoconfig.db.mybatis.PGJsonTypeHandler }
   )
   </foreach>
   on conflict do nothing
   </script>
               """)
               fun mybatisInsertBatchPG(list: Collection<DOTbTest1>)
           }
       
//@Service
open class AutoServiceTbTest1: ServiceImpl<AutoMapperTbTest1, DOTbTest1>()