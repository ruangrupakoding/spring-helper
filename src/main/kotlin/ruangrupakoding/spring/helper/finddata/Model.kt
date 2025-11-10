package ruangrupakoding.spring.helper.finddata

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Transient
import ruangrupakoding.spring.helper.Helper
import java.lang.reflect.Field

enum class Criteria {
    EQ, // EQUAL
    NEQ, // NOT EQUAL
    LIKE,
    GT, // GREATER THAN
    LT, // LESS THAN
    GTE, // GREATER THAN EQUAL
    LTE, // LESS THAN EQUAL
    ISNULL,
    ISNOTNULL,
    IN,
    NOTIN,
    GTDT,
    GTEDT,
    LTDT,
    LTEDT,
    GTD,
    GTED,
    LTD,
    LTED,
    GTT,
    GTET,
    LTT,
    LTET,
}

class FilterData(
    val field: String? = null,
    val criteria: Criteria? = null,
    val value: String? = null,
    val or: Array<FilterData>? = null,
    val and: Array<FilterData>? = null,
) {
    companion object {
        fun fromJson(json: String): FilterData? {
            return try {
                Helper.readValue(json, FilterData::class.java)
            } catch (e: Exception) {
                null
            }
        }

        fun toJson(filterData: FilterData): String? {
            return try {
                Helper.writeValueAsString(filterData)
            } catch (e: Exception) {
                null
            }
        }

        fun filter(field: String, criteria: Criteria, value: String): FilterData = FilterData(field, criteria, value)

        fun or(vararg fd: FilterData): FilterData = FilterData(or = arrayOf(*fd))
        fun and(vararg fd: FilterData): FilterData = FilterData(and = arrayOf(*fd))
    }
}

class JoinObject(val name: String, val clazz: Class<*>, val alias: String = "")

fun List<Field>.findId(): Field {
    return this.first { s -> s.annotations.any { a -> a is Id } }
}

fun List<Field>.defaultSelectField(): List<Field> {
    return this.filter {
        !it.annotations.any { a ->
            a is ManyToMany || a is ManyToOne || a is OneToMany || a is OneToOne || a is Transient || a is JsonIgnore //|| a is NaturalId
        }
    }
}

fun List<Field>.columnSelected(): List<String> {
    return this.map { it.name }
}