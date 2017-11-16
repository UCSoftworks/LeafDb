package com.ucsoftworks.leafdb.querying.internal

import com.ucsoftworks.leafdb.querying.ILeafDbQuery
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
internal class LeafDbStringQuery(internal val query: String, private val leafDbProvider: ILeafDbProvider) : ILeafDbQuery<String?> {


    override fun execute(): String? {
        val readableDb = leafDbProvider.readableDb
        val cursor = readableDb.selectQuery(query)
        if (cursor.empty)
            return null
        val string = cursor.getString(1)
        readableDb.close()
        return string

    }

    fun <T> map(clazz: Class<T>) = LeafDbValueQuery<T>(this, clazz)

}
