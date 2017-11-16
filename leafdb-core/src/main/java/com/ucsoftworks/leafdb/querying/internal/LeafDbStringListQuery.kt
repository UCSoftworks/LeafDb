package com.ucsoftworks.leafdb.querying.internal

import com.ucsoftworks.leafdb.collectIndexedStrings
import com.ucsoftworks.leafdb.querying.ILeafDbQuery
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
internal class LeafDbStringListQuery(internal val query: String, private val leafDbProvider: ILeafDbProvider) : ILeafDbQuery<List<String>> {

    override fun execute(): List<String> {
        val readableDb = leafDbProvider.readableDb
        val cursor = readableDb.selectQuery(query)
        val collectStrings = cursor.collectIndexedStrings(1)
        readableDb.close()
        return collectStrings.map { it.second }
    }


}
