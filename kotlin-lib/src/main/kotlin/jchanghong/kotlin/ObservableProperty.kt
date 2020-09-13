package jchanghong.kotlin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

val log: Logger = LoggerFactory.getLogger("jchanghong.kotlin.NotNullProperty")

/**
 * 拒绝null值
 */
public class NotNullProperty<T>(initialValue: T) {
    private var value: T = initialValue
    public operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    public operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (thisRef == null) return
        if (value === null) {
            log.info("${thisRef::class.simpleName}.${property.name} is null 拒绝")
            return
        }
        this.value = value
    }
}