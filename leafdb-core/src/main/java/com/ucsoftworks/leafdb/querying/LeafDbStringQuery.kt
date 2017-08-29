package com.ucsoftworks.leafdb.querying

import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import io.reactivex.Single

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
class LeafDbStringQuery internal constructor(internal val query: String, private val leafDbProvider: ILeafDbProvider) {


    fun execute(): String? {
        val readableDb = leafDbProvider.readableDb
        val cursor = readableDb.selectQuery(query)
        if (cursor.empty)
            return null
        val string = cursor.getString(1)
        readableDb.close()
        return string

    }

    fun asSingle(): Single<String> = Single.create {
        try {
            val t = execute()
            if (t == null)
                it.onError(NullPointerException("No result"))
            else
                it.onSuccess(t)
        } catch (e: Throwable) {
            it.onError(e)
        }
    }

    fun <T> map(clazz: Class<T>) = LeafDbValueQuery<T>(this, clazz)

}
