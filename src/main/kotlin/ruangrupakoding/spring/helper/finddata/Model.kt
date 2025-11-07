package ruangrupakoding.spring.helper.finddata

enum class Criteria(val query: String) {
    EQ (query = " = "), // EQUAL
    NEQ (query = " != "), // NOT EQUAL
    LIKE (query = " LIKE "),
    GT (query = " > "), // GREATER THAN
    LT (query = " < "), // LESS THAN
    GTE (query = " >= "), // GREATER THAN EQUAL
    LTE (query = " <= "), // LESS THAN EQUAL
    ISNULL (query = " ISNULL "),
    ISNOTNULL (query = " ISNOTNULL "),
    IN (query = " IN "),
    NOTIN (query = " NOTIN ")
}

class FindData(
    val field: String? = null,
    val criteria: Criteria? = null,
    val or: Array<FindData>? = null,
    val and: Array<FindData>? = null,
    vararg value: String
)

class JoinObject(val name: String, val clazz: Class<*>, val alias: String = "")