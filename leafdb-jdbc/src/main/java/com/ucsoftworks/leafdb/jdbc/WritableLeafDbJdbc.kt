package com.ucsoftworks.leafdb.jdbc

import com.ucsoftworks.leafdb.wrapper.ILeafDbWritableDatabase
import java.sql.Statement

/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
internal class WritableLeafDbJdbc(private val writableDatabase: Statement) : ILeafDbWritableDatabase {

    override fun modificationQuery(query: String) {
        writableDatabase.execute(query)
    }

    override fun close() {
        writableDatabase.close()
    }

}
