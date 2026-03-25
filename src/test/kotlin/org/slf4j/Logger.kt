package org.slf4j

interface Logger {
    fun info(msg: String)
    fun info(format: String, arg: Any)
    fun debug(msg: String)
    fun warn(msg: String)
    fun error(msg: String)
}

object LoggerFactory {
    fun getLogger(clazz: Class<*>): Logger = object : Logger {
        override fun info(msg: String) {}
        override fun info(format: String, arg: Any) {}
        override fun debug(msg: String) {}
        override fun warn(msg: String) {}
        override fun error(msg: String) {}
    }
}
