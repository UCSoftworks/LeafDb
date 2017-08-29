package com.ucsoftworks.leafdb.android

import com.ucsoftworks.leafdb.wrapper.ILeafDbCursor
import com.ucsoftworks.leafdb.wrapper.ILeafDbReadableDatabase
import io.requery.android.database.sqlite.SQLiteDatabase

/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
internal class ReadableLeafDbAndroid(private val readableDatabase: SQLiteDatabase) : ILeafDbReadableDatabase {

    val FIRST_COLUMN_ID = 1

    override fun close() {
        readableDatabase.close()
    }

    override fun selectQuery(query: String): ILeafDbCursor = LeafDbAndroidCursor(readableDatabase.rawQuery(query, null))


    override fun stringQuery(query: String): String? {
        val cursor = LeafDbAndroidCursor(readableDatabase.rawQuery(query, null))
        if (cursor.empty)
            return null
        return cursor.getString(FIRST_COLUMN_ID)
    }

    override fun longQuery(query: String): Long? {
        val cursor = LeafDbAndroidCursor(readableDatabase.rawQuery(query, null))
        if (cursor.empty)
            return null
        return cursor.getLong(FIRST_COLUMN_ID)
    }

    override fun doubleQuery(query: String): Double? {
        val cursor = LeafDbAndroidCursor(readableDatabase.rawQuery(query, null))
        if (cursor.empty)
            return null
        return cursor.getDouble(FIRST_COLUMN_ID)
    }

}
