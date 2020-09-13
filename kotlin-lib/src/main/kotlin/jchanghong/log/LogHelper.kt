package jchanghong.log

import jchanghong.log.LogHelper.merror
import jchanghong.log.LogHelper.minfo
import org.slf4j.LoggerFactory

private class T1 {
    init {
        minfo("test")
    }
}

private class T2 {
    init {
        merror("errro", NullPointerException("null"))
    }
}

fun main() {
    val logger1 = T2()
    val logger2 = T2()
    println(logger2 === logger1)
}

object LogHelper {

    const val logMsg = "||"
    fun Any.minfo(message: String): Unit {
        LoggerFactory.getLogger(this.javaClass).info(logMsg + message)
    }

    fun Any.mdebug(message: String): Unit {
        LoggerFactory.getLogger(this.javaClass).debug(logMsg + message)
    }

    @JvmOverloads
    fun Any.merror(message: String, exception: java.lang.Exception? = null): Unit {
        val msg = exception?.cause?.message?.replace("\n", "") ?: ""
        LoggerFactory.getLogger(this.javaClass).error(logMsg + message + msg)
    }

    fun Any.mwarn(message: String): Unit {
        LoggerFactory.getLogger(this.javaClass).warn(message)
    }
}
