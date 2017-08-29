package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.jdbc.LeafDbJdbcHelper

/**
 * Created by Pasenchuk Victor on 28/08/2017
 */
class SampleHelper(dbName: String, version: Int) : LeafDbJdbcHelper(dbName, version) {

    override fun onCreate(): LeafDbSchemaMigration =
            LeafDbSchemaMigration()
                    .createTable(Tables.TEST.name)


    override fun onUpgradeSchema(oldVersion: Int, newVersion: Int): LeafDbSchemaMigration =
            when (oldVersion) {
                1 -> LeafDbSchemaMigration().createTable(Tables.LOAD.tableName)
                else -> LeafDbSchemaMigration()
            }

}
