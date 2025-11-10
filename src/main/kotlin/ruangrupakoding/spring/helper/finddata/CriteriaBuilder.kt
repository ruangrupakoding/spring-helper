package ruangrupakoding.spring.helper.finddata

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.From
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object HelperCriteriaBuilder {

    private val patternLocalDatetime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val patternLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val patternLocalTime = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun <T> toPredicate(
        find: FilterData?,
        cb: CriteriaBuilder,
        root: Root<T>,
        joins: MutableMap<String, Join<*, *>>
    ): Predicate? {
        if (find == null) return null
        return when {
            find.field != null && find.criteria != null && find.value != null -> {
                val path =  resolvePath(root, find.field, joins) //root.get<Any>(find.field)
                when (find.criteria) {
                    Criteria.NEQ -> cb.notEqual(path, find.value)
                    Criteria.LIKE -> cb.like(
                        cb.lower(path as Expression<String>),
                        "%${find.value.lowercase()}%"
                    )

                    Criteria.IN -> path.`in`(find.value.split(";"))
                    Criteria.NOTIN -> cb.not(path.`in`(find.value.split(";")))
                    Criteria.ISNULL -> cb.isNull(path)
                    Criteria.ISNOTNULL -> cb.isNotNull(path)
                    Criteria.GT -> cb.greaterThan(path as Expression<Double>, find.value.toDouble())
                    Criteria.GTE -> cb.greaterThanOrEqualTo(
                        path as Expression<Double>,
                        find.value.toDouble()
                    )

                    Criteria.LT -> cb.lessThan(path as Expression<Double>, find.value.toDouble())
                    Criteria.LTE -> cb.lessThanOrEqualTo(
                        path as Expression<Double>,
                        find.value.toDouble()
                    )

                    Criteria.GTDT -> cb.greaterThan(
                        path as Expression<LocalDateTime>, LocalDateTime.parse(
                            find.value, patternLocalDatetime
                        )
                    )
                    Criteria.GTEDT -> cb.greaterThanOrEqualTo(
                        path as Expression<LocalDateTime>, LocalDateTime.parse(
                            find.value, patternLocalDatetime
                        )
                    )

                    Criteria.LTDT -> cb.lessThan(
                        path as Expression<LocalDateTime>, LocalDateTime.parse(
                            find.value, patternLocalDatetime
                        )
                    )
                    Criteria.LTEDT -> cb.lessThanOrEqualTo(
                        path as Expression<LocalDateTime>, LocalDateTime.parse(
                            find.value, patternLocalDatetime
                        )
                    )

                    Criteria.GTD -> cb.greaterThan(
                        path as Expression<LocalDate>, LocalDate.parse(
                            find.value,
                            patternLocalDate
                        )
                    )
                    Criteria.GTED -> cb.greaterThanOrEqualTo(
                        path as Expression<LocalDate>, LocalDate.parse(
                            find.value,
                            patternLocalDate
                        )
                    )

                    Criteria.LTD -> cb.lessThan(
                        path as Expression<LocalDate>, LocalDate.parse(
                            find.value,
                            patternLocalDate
                        )
                    )
                    Criteria.LTED -> cb.lessThanOrEqualTo(
                        path as Expression<LocalDate>, LocalDate.parse(
                            find.value,
                            patternLocalDate
                        )
                    )

                    Criteria.GTT -> cb.greaterThan(
                        path as Expression<LocalTime>, LocalTime.parse(
                            find.value,
                            patternLocalTime
                        )
                    )
                    Criteria.GTET -> cb.greaterThanOrEqualTo(
                        path as Expression<LocalTime>, LocalTime.parse(
                            find.value,
                            patternLocalTime
                        )
                    )

                    Criteria.LTT -> cb.lessThan(
                        path as Expression<LocalTime>, LocalTime.parse(
                            find.value,
                            patternLocalTime
                        )
                    )
                    Criteria.LTET -> cb.lessThanOrEqualTo(
                        path as Expression<LocalTime>, LocalTime.parse(
                            find.value,
                            patternLocalTime
                        )
                    )

                    else -> cb.equal(path, find.value)
                }
            }
            find.or != null -> {
                val filterOr = find.or.mapNotNull { toPredicate(it, cb, root, joins) }
                cb.or(*filterOr.toTypedArray())
            }
            find.and != null -> {
                val filterOr = find.and.mapNotNull { toPredicate(it, cb, root, joins) }
                cb.and(*filterOr.toTypedArray())
            }
            else -> null
        }
    }

    fun <T> resolvePath(root: Root<T>, fieldName: String, joins: MutableMap<String, Join<*, *>>): Path<*> {
        if (!fieldName.contains(".")) {
            return root.get<Any>(fieldName)
        }

        val parts = fieldName.split(".")
        var join: From<*, *> = root

        // Loop setiap bagian kecuali terakhir
        for (i in 0 until parts.size - 1) {
            val joinName = parts[i]
            // kalau belum ada join, buat baru
            join = joins.computeIfAbsent(joinName) {
                join.join<Any, Any>(joinName, JoinType.LEFT)
            }
        }

        return join.get<Any>(parts.last())
    }
}