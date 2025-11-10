package ruangrupakoding.spring.helper.csv

import com.fasterxml.jackson.dataformat.csv.CsvGenerator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import ruangrupakoding.spring.helper.formatToText
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CsvWriter (private val data: MutableList<*>) {

    private var header: Array<String> = arrayOf()
    private var showHeader: Boolean = false
    private var firstLine: String? = null

    fun setHeader(vararg i: String) = apply { header = arrayOf(*i) }
    fun showHeader(i: Boolean) = apply { showHeader = i }
    fun setFirstLine(i: String) = apply { firstLine = i }

    fun toCsvString() : String {
        return getCsvString()
    }
    fun toCsvByteArray() : ByteArray {
        return getCsvString().toByteArray()
    }

    private fun getCsvString() : String {
        return "${firstLine ?: ""} " +
                "\n${listToCsvString()}"
    }


    private fun listToCsvString(): String {
        return when {
            data.isEmpty() -> ""
            else -> {
                val mapKeys: MutableList<String> = mutableListOf()
                val dtToListMap = data.map { dt ->
                    dt as Serializable
                    dt.javaClass.declaredFields
                        .filter { f ->
                            !f.annotations.any { a ->
                                a.toString().contains("JsonIgnore") || a.toString().contains("Transient")
                            }
                        }
                        .filter { f -> !f.type.name.endsWith("\$Companion") }
                        .map { f ->
                            f.isAccessible = true
                            Pair(f.name, f.get(dt))
                        }
                        .filter { p ->
                            if (header.isNotEmpty()) header.any { h -> p.first == h } else true
                        }.associate { p ->
                            val v = when (val dtVal = p.second) {
                                null -> ""
                                is LocalDateTime -> dtVal.formatToText()
                                is LocalTime -> dtVal.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                                is LocalDate -> dtVal.formatToText()
                                else -> dtVal.toString().trim().replace("\n", " ")
                            }
                            mapKeys.add(p.first)
                            Pair(p.first, v)
                        }.toMutableMap()
                }.toMutableList()

                val schemaBuilder = CsvSchema.builder()

                if (header.isNotEmpty()) {
                    header.forEach { schemaBuilder.addColumn(it) }
                } else {
                    dtToListMap[0].entries.forEach {
                        schemaBuilder.addColumn(it.key)
                    }
                }
                val sch = schemaBuilder.build().withNullValue("\"\"")//.withHeader()
                val mapper = CsvMapper()
                val dataCsvString = mapper.enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS).writer(sch)
                    .writeValueAsString(dtToListMap)
                val headerString = when {
                    !showHeader -> ""
                    header.isNotEmpty() -> "${header.joinToString(",") {"\"$it\""}}\n"
                    else -> "${mapKeys.distinct().joinToString(",") {"\"$it\""}}\n"
                }

                return "${headerString}$dataCsvString"
            }
        }
    }

}