# utils
封装自己常用工具包

使用
```$xslt
 <dependency>
      <groupId>com.github.jchanghong</groupId>
      <artifactId>utils</artifactId>
      <version>3.0.0</version>
    </dependency>
```
maven依赖全是provide，不是传递依赖，需要相应功能的时候需要引入
这样可以自定义依赖版本
spring mybatis多数据源
不用到处copy配置，只需要配置数据库信息就可
```java

datac.url=jdbc:postgresql://localhost:5435/datac
datac.username=postgres
datac.password=rrrrr

  @Primary
  @Bean(name = "sqlSessionFactoryDATAC")
  public  SqlSessionFactory getinitdatac(){
    return DBHelper.mybatisPostgres("datac");
  }
```
查询任意sql语句，结果存储到新表。不用新建表。一条语句就可
```java
 String sql="select * from table1";
    DBHelper.create2Table().create(sqlSessionFactoryimp,
     sqlSessionFactory, sql, "test12");
```
查询任意sql语句，结果直接返回为json，直接返回前端数据，支持分页，一条语句完成所有功能

```java
DBHelper.table2json(sqlSessionFactory, "select * from table1", 1, 2)
```

