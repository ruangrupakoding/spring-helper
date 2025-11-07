package ruangrupakoding.spring.helper

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Helper {
    private val mapper = jacksonObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .registerModule(Jdk8Module())
        .registerModule(ParameterNamesModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun writeValueAsString(data: Any) : String {
        return mapper.writeValueAsString(data)
    }

    fun <T> readValue(value: String, clazz: Class<T>): T = mapper.readValue(value, clazz)
}

fun LocalDate.formatToText(pattern: String? = null) : String? {
    return try {
        this.format(DateTimeFormatter.ofPattern(pattern ?: "yyyy-MM-dd"))
    } catch (_: Exception) {
        null
    }
}

fun LocalDateTime.formatToText(pattern: String? = null) : String? {
    return try {
        this.format(DateTimeFormatter.ofPattern(pattern ?: "yyyy-MM-dd HH:mm:ss"))
    } catch (_: Exception) {
        null
    }
}

fun String.convertToDate(pattern: String? = null) : LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern ?: "yyyy-MM-dd"))
    } catch (_: Exception) {
        null
    }
}

fun String.convertToDateTime(pattern: String? = null) : LocalDateTime? {
    return try {
        LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern ?: "yyyy-MM-dd HH:mm:ss"))
    } catch (_: Exception) {
        null
    }
}