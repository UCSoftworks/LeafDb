package com.ucsoftworks.leafdb.dsl

/**
 * Created by Pasenchuk Victor on 10/08/2017
 */
class Field private constructor(internal val field: String) {

    companion object {
        private fun getPath(vararg path: String): String {
            if (path.isEmpty())
                return "$"
            return path.joinToString(".", prefix = "$.")
        }
    }

    constructor(vararg path: String) : this("json_extract(doc, '${Field.getPath(*path)}')")
    internal constructor(a: Field, operator: String, b: Field) : this("($a $operator $b)")
    internal constructor(a: Field, operator: String, b: Number) : this("($a $operator $b)")
    internal constructor(b: Number, operator: String, a: Field) : this("($b $operator $a)")


    operator fun unaryPlus() = Field("+$field")
    operator fun unaryMinus() = Field("-$field")

    operator fun plus(field: Field) = Field(this, "+", field)
    operator fun minus(field: Field) = Field(this, "-", field)
    operator fun times(field: Field) = Field(this, "*", field)
    operator fun div(field: Field) = Field(this, "/", field)
    operator fun rem(field: Field) = Field(this, "%", field)

    operator fun plus(number: Number) = Field(this, "+", number)
    operator fun minus(number: Number) = Field(this, "-", number)
    operator fun times(number: Number) = Field(this, "*", number)
    operator fun div(number: Number) = Field(this, "/", number)
    operator fun rem(number: Number) = Field(this, "%", number)

    infix fun greaterThan(a: Any?)
            = Condition.compareField(this, ComparisonOperator.GREATER_THAN, a)

    infix fun greaterThanOrEqual(a: Any?)
            = Condition.compareField(this, ComparisonOperator.GREATER_THAN_OR_EQUAL, a)

    infix fun lessThan(a: Any?)
            = Condition.compareField(this, ComparisonOperator.LESS_THAN, a)

    infix fun lessThanOrEqual(a: Any?)
            = Condition.compareField(this, ComparisonOperator.LESS_THAN_OR_EQUAL, a)

    infix fun notEqual(a: Any?)
            = Condition.compareField(this, ComparisonOperator.NOT_EQUAL, a)

    infix fun equal(a: Any?)
            = Condition.compareField(this, ComparisonOperator.EQUAL, a)

    fun isNull() = Condition.isNull(this)

    fun isNotNull()
            = Condition.isNotNull(this)

    infix fun <T : Comparable<T>> between(interval: ClosedRange<T>)
            = Condition.between(this, interval.start, interval.endInclusive)

    infix fun like(pattern: String)
            = Condition.like(this, pattern)

    infix fun contains(query: String)
            = this.like("%$query%")

    infix fun startsWith(query: String)
            = this.like("$query%")

    infix fun endsWith(query: String)
            = this.like("$query%")

    override fun toString() = field

}
