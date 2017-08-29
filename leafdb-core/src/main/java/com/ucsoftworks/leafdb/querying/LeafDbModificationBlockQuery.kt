package com.ucsoftworks.leafdb.querying

import io.reactivex.Single

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
open internal class LeafDbModificationBlockQuery internal constructor(private val block: () -> Unit) : ILeafDbModificationQuery {

    override fun execute() {
        block()
    }

    override fun asSingle(): Single<Unit> = Single.create {
        try {
            execute()
            it.onSuccess(Unit)
        } catch (e: Exception) {
            it.onError(e)
        }
    }

}
