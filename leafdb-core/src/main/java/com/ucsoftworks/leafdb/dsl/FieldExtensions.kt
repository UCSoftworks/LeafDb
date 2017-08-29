package com.ucsoftworks.leafdb.dsl

/**
 * Created by Pasenchuk Victor on 11/08/2017
 */

operator fun Number.plus(field: Field) = Field(this, "+", field)

operator fun Number.minus(field: Field) = Field(this, "-", field)
operator fun Number.times(field: Field) = Field(this, "*", field)
operator fun Number.div(field: Field) = Field(this, "/", field)
operator fun Number.rem(field: Field) = Field(this, "%", field)
