package com.ucsoftworks.leafdb.wrapper

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
interface ILeafDbCursor {

    val empty: Boolean

    val isClosed: Boolean

    fun close()

    fun moveToNext(): Boolean

    fun getString(index: Int): String

    fun getLong(index: Int): Long

    fun getDouble(index: Int): Double

}
