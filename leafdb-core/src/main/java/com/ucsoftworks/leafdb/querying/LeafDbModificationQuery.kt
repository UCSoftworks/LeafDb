package com.ucsoftworks.leafdb.querying

import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
internal class LeafDbModificationQuery internal constructor(private val table: String, private val query: String, private val leafDbProvider: ILeafDbProvider, private val notifySubscribers: Boolean = true) : LeafDbModificationBlockQuery({
    val writableDb = leafDbProvider.writableDb
    writableDb.modificationQuery(query)
    writableDb.close()
    if (notifySubscribers)
        leafDbProvider.notifySubscribers(table)
})
