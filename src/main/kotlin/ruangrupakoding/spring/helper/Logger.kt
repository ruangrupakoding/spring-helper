package ruangrupakoding.spring.helper

import java.util.logging.Level
import java.util.logging.Logger

class HelperLogger private constructor(val logger: Logger) {
    fun info(info: String) {
        logger.log(Level.INFO, info)
    }

    fun error(message: String, error: Throwable) {
        logger.log(Level.SEVERE, message, error)
    }

    companion object {
        fun getLogger(name: String): HelperLogger {
            return HelperLogger(Logger.getLogger(name))
        }

        fun getLogger(cls: Class<*>): HelperLogger {
            return getLogger(cls.name)
        }
    }
}