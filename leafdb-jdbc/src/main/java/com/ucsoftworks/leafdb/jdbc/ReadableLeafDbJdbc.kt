package com.ucsoftworks.leafdb.jdbc

import com.ucsoftworks.leafdb.wrapper.ILeafDbCursor
import com.ucsoftworks.leafdb.wrapper.ILeafDbReadableDatabase
import java.sql.Statement

/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
internal class ReadableLeafDbJdbc(private val readableDatabase: Statement) : ILeafDbReadableDatabase {

    val FIRST_COLUMN_ID = 1

    override fun close() {
        readableDatabase.close()
    }

    override fun selectQuery(query: String): ILeafDbCursor = LeafDbJdbcCursor(readableDatabase.executeQuery(query))


    override fun stringQuery(query: String): String? {
        val cursor = LeafDbJdbcCursor(readableDatabase.executeQuery(query))
        if (cursor.empty)
            return null
        return cursor.getString(FIRST_COLUMN_ID)
    }

    override fun longQuery(query: String): Long? {
        val cursor = LeafDbJdbcCursor(readableDatabase.executeQuery(query))
        if (cursor.empty)
            return null
        return cursor.getLong(FIRST_COLUMN_ID)
    }

    override fun doubleQuery(query: String): Double? {
        val cursor = LeafDbJdbcCursor(readableDatabase.executeQuery(query))
        if (cursor.empty)
            return null
        return cursor.getDouble(FIRST_COLUMN_ID)
    }

}
