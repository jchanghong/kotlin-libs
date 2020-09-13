package jchanghong.test

import jchanghong.cache.CacheHelp
import jchanghong.file.FileHelper
import java.util.function.Supplier

class Test1 {
    val map = CacheHelp.newCronMap<String, String>("1/3 * * * * ?")

    init {
        map.put2(Test1::a.name, Supplier {
            println("do get a ...")
            "2"
        })
        map.put2(Test1::b.name, Supplier {
            println("do get a ...")
            "3"
        })
    }

    val a: String by map
    val b: String by map
}

fun main() {
    FileHelper.removeMavenLastAndRemoteFiles("D:/mavendir")
}