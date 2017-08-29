package com.ucsoftworks.leafdb.android

import com.ucsoftworks.leafdb.wrapper.ILeafDbWritableDatabase
import io.requery.android.database.sqlite.SQLiteDatabase

/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
internal class WritableLeafDbAndroid(private val writableDatabase: SQLiteDatabase) : ILeafDbWritableDatabase {

    override fun modificationQuery(query: String) {
        writableDatabase.execSQL(query)
    }

    override fun close() {
        writableDatabase.close()
    }

}
