package com.ucsoftworks.leafdb.querying

import io.reactivex.Flowable
import io.reactivex.Single

interface ILeafDbQuery<T> {

    fun execute(): T

    fun asSingle(): Single<T> = Single.create {
        try {
            it.onSuccess(execute())
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    fun asFlowable(): Flowable<T> = asSingle().toFlowable()

}
