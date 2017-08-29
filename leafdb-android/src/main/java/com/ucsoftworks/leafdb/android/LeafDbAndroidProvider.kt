package com.ucsoftworks.leafdb.android

import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import com.ucsoftworks.leafdb.wrapper.ILeafDbReadableDatabase
import com.ucsoftworks.leafdb.wrapper.ILeafDbWritableDatabase

/**
 * Created by Pasenchuk Victor on 26/08/2017
 */
internal class LeafDbAndroidProvider internal constructor(private val leadDbAndroidHelper: LeafDbAndroidHelper) : ILeafDbProvider {

    override val listQueries = mutableListOf<ILeafDbProvider.ListQuery>()

    override val readableDb: ILeafDbReadableDatabase
        get() = ReadableLeafDbAndroid(leadDbAndroidHelper.readableDatabase)

    override val writableDb: ILeafDbWritableDatabase
        get() = WritableLeafDbAndroid(leadDbAndroidHelper.writableDatabase)

}
