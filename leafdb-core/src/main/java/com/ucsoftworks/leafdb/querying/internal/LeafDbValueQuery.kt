package com.ucsoftworks.leafdb.querying.internal

import com.ucsoftworks.leafdb.querying.ILeafDbQuery
import com.ucsoftworks.leafdb.serializer.Serializer

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
internal class LeafDbValueQuery<T>(internal val leafDbStringQuery: LeafDbStringQuery, private val clazz: Class<T>) : ILeafDbQuery<T> {

    private val serializer = Serializer()

    override fun execute(): T = serializer.getObjectFromJson(leafDbStringQuery.execute(), clazz)

}
