package com.ucsoftworks.leafdb.wrapper

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
interface ILeafDbReadableDatabase : ILeafDbDatabase {

    fun selectQuery(query: String): ILeafDbCursor
    fun stringQuery(query: String): String?
    fun longQuery(query: String): Long?
    fun doubleQuery(query: String): Double?

}
