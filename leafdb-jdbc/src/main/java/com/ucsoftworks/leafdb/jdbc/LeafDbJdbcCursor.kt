package com.ucsoftworks.leafdb.jdbc

import com.ucsoftworks.leafdb.wrapper.ILeafDbCursor
import java.sql.ResultSet

/**
 * Created by Pasenchuk Victor on 28/08/2017
 */
class LeafDbJdbcCursor(private val cursor: ResultSet) : ILeafDbCursor {

    override val empty: Boolean
        get() = !cursor.next()

    override val isClosed: Boolean
        get() = cursor.isClosed

    override fun close() {
        cursor.close()
    }

    override fun moveToNext() =
            cursor.next()

    override fun getString(index: Int): String
            = cursor.getString(index)

    override fun getLong(index: Int): Long
            = cursor.getLong(index)

    override fun getDouble(index: Int): Double
            = cursor.getDouble(index)

}
