package com.ucsoftworks.leafdb.querying

import com.ucsoftworks.leafdb.collectIndexedStrings
import com.ucsoftworks.leafdb.serializer.Serializer
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
class LeafDbListQuery<T> internal constructor(private val table: String, private val query: String, private val leafDbProvider: ILeafDbProvider, private val clazz: Class<T>) {

    private val DOC_POSITION = 1

    private val serializer = Serializer()

    fun execute(): List<T> {
        val readableDb = leafDbProvider.readableDb
        val list = readableDb
                .selectQuery(query)
                .collectIndexedStrings(DOC_POSITION)
                .map { serializer.getObjectFromJson(it.second, clazz) }
        readableDb.close()
        return list
    }

    fun asSingle(): Single<List<T>> = Single.create {
        try {
            it.onSuccess(execute())
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    fun asFlowable(): Flowable<List<T>> = Flowable.create({
        val db = leafDbProvider.readableDb
        val cb = {
            try {
                it.onNext(db
                        .selectQuery(query)
                        .collectIndexedStrings(DOC_POSITION)
                        .map { serializer.getObjectFromJson(it.second, clazz) }
                )
            } catch (e: Exception) {
                it.onError(e)
            }
        }

        cb()

        val query = ILeafDbProvider.ListQuery(table, cb)
        leafDbProvider.listQueries.add(query)

        it.setCancellable {
            leafDbProvider.listQueries.remove(query)
            db.close()
        }
    }, BackpressureStrategy.LATEST)

}
