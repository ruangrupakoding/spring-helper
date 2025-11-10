package ruangrupakoding.spring.helper.finddata

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Root

class HelperQuery private constructor() {

    data class Builder<T> (val entityManager: EntityManager, val clazz: Class<T>) {
        fun findDataList(
            select: List<String>? = null,
            filter: FilterData? = null,
            page: Int = 1,
            size: Int = 10,
        ): List<Map<String, Any?>> {
            return findData(select, filter, page, size)
        }

        fun findById(
            id: Any,
            select: List<String>? = null
        ): Map<String, Any?>? {
            return findData(select, FilterData.filter("id", Criteria.EQ, "$id")).firstOrNull()
        }

        fun findAll(
            select: List<String>? = null,
            filter: FilterData? = null
        ): List<Map<String, Any?>> {
            return findData(select, filter, size = Int.MAX_VALUE)
        }

        fun count(filter: FilterData? = null) : Int {
            val select = clazz.declaredFields.toList().findId().name
            return findData(listOf(select), filter).size
        }

        private fun findData(
            select: List<String>? = null,
            filter: FilterData? = null,
            page: Int = 1,
            size: Int = 10,
        ): List<Map<String, Any?>> {
            val cb: CriteriaBuilder = entityManager.criteriaBuilder
            val cq: CriteriaQuery<Tuple> = cb.createTupleQuery()
            val root: Root<T> = cq.from(clazz)
            val joins = mutableMapOf<String, Join<*, *>>()

            val columnSelected = select ?: clazz.declaredFields.toList().defaultSelectField().columnSelected()
            val selections = columnSelected.map {
                HelperCriteriaBuilder.resolvePath(root, it, joins).alias(it)
                //root.get<Any>(it).alias(it)
            }
            cq.multiselect(selections)

            val predicate = HelperCriteriaBuilder.toPredicate(filter, cb, root, joins)

            if (predicate != null) cq.where(predicate)

            val query = entityManager.createQuery(cq)
            query.firstResult = (page - 1) * size
            query.maxResults = size
            val result = query.resultList

            // Convert Tuple → Map<String, Any>
            return result.map { tuple -> columnSelected.associateWith { tuple[it] } }
        }
    }

}