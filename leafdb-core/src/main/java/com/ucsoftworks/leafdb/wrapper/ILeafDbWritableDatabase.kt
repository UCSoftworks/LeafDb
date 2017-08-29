package com.ucsoftworks.leafdb.wrapper

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
interface ILeafDbWritableDatabase : ILeafDbDatabase {

    fun modificationQuery(query: String)

}
