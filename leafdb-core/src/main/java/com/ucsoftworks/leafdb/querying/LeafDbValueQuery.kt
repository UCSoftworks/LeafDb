package com.ucsoftworks.leafdb.querying

import com.ucsoftworks.leafdb.serializer.Serializer
import io.reactivex.Single

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
class LeafDbValueQuery<T> internal constructor(internal val leafDbStringQuery: LeafDbStringQuery, private val clazz: Class<T>) {

    private val serializer = Serializer()

    fun execute(): T? = serializer.getObjectFromJson(leafDbStringQuery.execute(), clazz)

    fun asSingle(): Single<T> = leafDbStringQuery.asSingle().map<T> { serializer.getObjectFromJson(it, clazz)!! }

}
