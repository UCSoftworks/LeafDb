package com.ucsoftworks.leafdb.jdbc

import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import com.ucsoftworks.leafdb.wrapper.ILeafDbReadableDatabase
import com.ucsoftworks.leafdb.wrapper.ILeafDbWritableDatabase

/**
 * Created by Pasenchuk Victor on 26/08/2017
 */
internal class LeafDbJdbcProvider internal constructor(private val dbJdbcHelper: LeafDbJdbcHelper) : ILeafDbProvider {

    override val listQueries = mutableListOf<ILeafDbProvider.ListQuery>()

    override val readableDb: ILeafDbReadableDatabase
        get() = ReadableLeafDbJdbc(dbJdbcHelper.readableDb)

    override val writableDb: ILeafDbWritableDatabase
        get() = WritableLeafDbJdbc(dbJdbcHelper.writableDb)

}
