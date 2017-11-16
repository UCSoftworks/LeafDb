package querying

import com.ucsoftworks.leafdb.dsl.Condition
import com.ucsoftworks.leafdb.dsl.Field
import com.ucsoftworks.leafdb.querying.internal.LeafDbListQuery
import com.ucsoftworks.leafdb.dsl.SortOrder
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider

/**
 * Created by Pasenchuk Victor on 14/08/2017
 */


class SelectQueryBuilder internal constructor(private val table: String, private val leafDbProvider: ILeafDbProvider) {

    internal var field: Field = Field()
    internal var distinct: Boolean = false
    internal var condition: String? = null
    internal var orderBy: String? = null
    internal var order: SortOrder = SortOrder.ASC
    internal var limit: Int? = null
    internal var offset: Int? = null

    fun distinct(distinct: Boolean = true): SelectQueryBuilder {
        this.distinct = distinct
        return this
    }

    fun field(field: Field = Field()): SelectQueryBuilder {
        this.field = field
        return this
    }

    fun where(condition: Condition?): SelectQueryBuilder {
        this.condition = condition?.condition
        return this
    }


    fun orderBy(orderBy: Field?, sortOrder: SortOrder = SortOrder.ASC): SelectQueryBuilder {
        this.orderBy = orderBy?.field
        this.order = sortOrder
        return this
    }

    fun limit(limit: Int?): SelectQueryBuilder {
        this.limit = limit
        return this
    }

    fun offset(offset: Int?): SelectQueryBuilder {
        this.offset = offset
        return this
    }

    fun <T> findAs(clazz: Class<T>) = LeafDbListQuery<T>(table, createSelectSqlString(), leafDbProvider, clazz)

    internal fun createSelectSqlString() = buildString {
        append("SELECT")
        if (distinct)
            append(" DISTINCT")
        append(" ${field.field} FROM $table")
        if (condition != null)
            append(" WHERE $condition")
        if (orderBy != null)
            append(" ORDER BY $orderBy $order")
        if (limit != null)
            append(" LIMIT $limit")
        if (offset != null)
            append(" OFFSET $offset")
        append(";")
    }

}
