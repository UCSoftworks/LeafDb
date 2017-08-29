package com.ucsoftworks.leafdb.wrapper

/**
 * Created by Pasenchuk Victor on 26/08/2017
 */
interface ILeafDbProvider {

    data class ListQuery(val table: String, val callback: () -> Unit)

    val readableDb: ILeafDbReadableDatabase

    val writableDb: ILeafDbWritableDatabase

    val listQueries: MutableList<ListQuery>

    fun notifySubscribers(table: String) {
        listQueries.filter { it.table == table }.forEach { it.callback() }
    }

}
