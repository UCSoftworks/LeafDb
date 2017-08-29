package com.ucsoftworks.leafdb.dsl

import com.ucsoftworks.leafdb.sqlValue

/**
 * Created by Pasenchuk Victor on 08/08/2017
 */
class Condition internal constructor(internal val condition: String) {

    infix fun or(subCondition: Condition) =
            Condition("($condition) OR ($subCondition)")

    infix fun and(subCondition: Condition) =
            Condition("($condition) AND ($subCondition)")

    operator fun not()
            = Condition("NOT ($condition)")

    override fun toString()
            = condition


    companion object {
        fun compareField(value1: Any?, comparisonOperator: ComparisonOperator, value2: Any?) =
                Condition("${value1.sqlValue} ${comparisonOperator.operator} ${value2.sqlValue}")

        fun compareField(field: Field, comparisonOperator: ComparisonOperator, value: Any?) =
                Condition("${field.field} ${comparisonOperator.operator} ${value.sqlValue}")

        fun isNull(field: Field) = Condition("${field.field} IS NULL")

        fun isNotNull(field: Field) = Condition("${field.field} IS NOT NULL")

        fun between(field: Field, a: Any?, b: Any?) =
                Condition("${field.field} BETWEEN ${a.sqlValue} AND ${b.sqlValue}")

        fun notBetween(field: Field, a: Any?, b: Any?) =
                Condition("${field.field} NOT BETWEEN ${a.sqlValue} AND ${b.sqlValue}")

        fun like(field: Field, pattern: String) =
                Condition("${field.field} LIKE ${pattern.sqlValue}")
    }
}