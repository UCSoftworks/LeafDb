package com.ucsoftworks.leafdb.jdbc

import com.ucsoftworks.leafdb.LeafDb
import com.ucsoftworks.leafdb.wrapper.ILeafDbHelper
import java.io.*
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

/**
 * Created by Pasenchuk Victor on 04/08/2017
 */
abstract class LeafDbJdbcHelper(override val dbName: String,
                                val version: Int) : ILeafDbHelper {


    override val leafDb: LeafDb
        get() = LeafDb(LeafDbJdbcProvider(this))

    val readableDb: Statement
        get() {
            val conn = connect()
            return conn.createStatement()
        }

    val writableDb: Statement
        get() {
            val conn = connect()
            return conn.createStatement()
        }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    private fun onCreate(db: Statement) {
        val migration = onCreate()

        for (sqlMigration in migration.sqlMigrations)
            db.execute(sqlMigration)
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    private fun onUpgrade(db: Statement, oldVersion: Int, newVersion: Int) {
        val migration = onUpgradeSchema(oldVersion, newVersion)
        for (sqlMigration in migration.sqlMigrations)
            db.execute(sqlMigration)

        val metaFile = File("$dbName.txt")
        writeVersion(metaFile)
    }

    private fun onDowngrade(db: Statement, oldVersion: Int, newVersion: Int) {
        val migration = onDowngradeSchema(oldVersion, newVersion)
        for (sqlMigration in migration.sqlMigrations)
            db.execute(sqlMigration)

        val metaFile = File("$dbName.txt")
        writeVersion(metaFile)
    }

    private fun connect(): Connection {
        Class.forName("org.sqlite.JDBC");
        // SQLite connection string
        val url = "jdbc:sqlite:$dbName"
        val conn = DriverManager.getConnection(url)

        val metaFile = File("$dbName.txt")
        if (metaFile.exists()) {
            val statement = conn.createStatement()
            val data_version = BufferedReader(InputStreamReader(FileInputStream(metaFile))).readLine().toInt()
            if (data_version < version)
                onUpgrade(statement, data_version, version)
            if (data_version > version)
                onDowngrade(statement, data_version, version)
            statement.close()
        } else {
            val statement = conn.createStatement()
            onCreate(statement)
            statement.close()

            writeVersion(metaFile)
        }

        return conn
    }

    private fun writeVersion(metaFile: File) {
        val writer = PrintWriter(metaFile)
        writer.write(version.toString())
        writer.flush()
        writer.close()
    }

}
