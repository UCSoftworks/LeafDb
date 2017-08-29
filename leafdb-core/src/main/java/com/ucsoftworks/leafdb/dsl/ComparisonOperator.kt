package com.ucsoftworks.leafdb.dsl

/**
 * Created by Pasenchuk Victor on 08/08/2017
 */
enum class ComparisonOperator(val operator: String) {

    EQUAL("="),
    NOT_EQUAL("<>"),

    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL(">="),

    LESS_THAN("<"),
    LESS_THAN_OR_EQUAL("<="),

}