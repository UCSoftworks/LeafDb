package com.ucsoftworks.leafdb.querying

import com.ucsoftworks.leafdb.collectIndexedStrings
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import io.reactivex.Single

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
class LeafDbStringListQuery internal constructor(internal val query: String, private val leafDbProvider: ILeafDbProvider) {


    fun execute(): List<String> {
        val readableDb = leafDbProvider.readableDb
        val cursor = readableDb.selectQuery(query)
        val collectStrings = cursor.collectIndexedStrings(1)
        readableDb.close()
        return collectStrings.map { it.second }
    }

    fun asSingle(): Single<List<String>> = Single.create {
        try {
            it.onSuccess(execute())
        } catch (e: Throwable) {
            it.onError(e)
        }
    }

}
