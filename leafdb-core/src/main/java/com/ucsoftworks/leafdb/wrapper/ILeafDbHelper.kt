package com.ucsoftworks.leafdb.wrapper

import com.ucsoftworks.leafdb.LeafDb
import com.ucsoftworks.leafdb.LeafDbSchemaMigration

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
interface ILeafDbHelper {

    val dbName: String

    val leafDb: LeafDb

    fun onCreate(): LeafDbSchemaMigration

    fun onUpgradeSchema(oldVersion: Int, newVersion: Int): LeafDbSchemaMigration

    fun onDowngradeSchema(oldVersion: Int, newVersion: Int): LeafDbSchemaMigration = LeafDbSchemaMigration()

}
