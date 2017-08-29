package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.dsl.Field

/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
class LeafDbSchemaMigration {

    val sqlMigrations = mutableListOf<String>()

    fun createTable(table: String): LeafDbSchemaMigration {
        sqlMigrations.add("CREATE TABLE $table (id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp INT, doc TEXT);")
        return this
    }

    fun dropTable(table: String): LeafDbSchemaMigration {
        sqlMigrations.add("DROP TABLE $table;")
        return this
    }


    fun createIndex(table: String, indexName: String, field: Field): LeafDbSchemaMigration {
        sqlMigrations.add("CREATE INDEX $indexName ON $table($field);")
        return this
    }

    fun dropIndex(indexName: String): LeafDbSchemaMigration {
        sqlMigrations.add("DROP INDEX IF EXISTS $indexName;")
        return this
    }

}
