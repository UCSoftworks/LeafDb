package com.ucsoftworks.leafdb.querying.internal

import com.ucsoftworks.leafdb.querying.ILeafDbQuery

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
open internal class LeafDbModificationBlockQuery(private val block: () -> Unit) : ILeafDbQuery<Unit> {

    override fun execute() {
        block()
    }

}
