package com.ucsoftworks.leafdb.querying

import io.reactivex.Single

/**
 * Created by Pasenchuk Victor on 27/08/2017
 */
interface ILeafDbModificationQuery {
    fun execute()
    fun asSingle(): Single<Unit>
}
