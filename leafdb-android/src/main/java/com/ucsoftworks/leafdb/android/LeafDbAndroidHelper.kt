package com.ucsoftworks.leafdb.android

import android.content.Context
import com.ucsoftworks.leafdb.LeafDb
import com.ucsoftworks.leafdb.wrapper.ILeafDbHelper
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.database.sqlite.SQLiteOpenHelper


/**
 * Created by Pasenchuk Victor on 04/08/2017
 */
abstract class LeafDbAndroidHelper(context: Context,
                                   override val dbName: String,
                                   version: Int)
    : SQLiteOpenHelper(context, dbName, null, version), ILeafDbHelper {

    override val leafDb: LeafDb
        get() = LeafDb(LeafDbAndroidProvider(this))

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    override fun onCreate(db: SQLiteDatabase) {
        val migration = onCreate()

        for (sqlMigration in migration.sqlMigrations)
            db.execSQL(sqlMigration)
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val migration = onUpgradeSchema(oldVersion, newVersion)
        for (sqlMigration in migration.sqlMigrations)
            db.execSQL(sqlMigration)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val migration = onDowngradeSchema(oldVersion, newVersion)
        for (sqlMigration in migration.sqlMigrations)
            db?.execSQL(sqlMigration)
    }

}
