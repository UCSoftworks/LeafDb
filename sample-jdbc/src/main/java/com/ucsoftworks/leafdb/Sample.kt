package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.dsl.Field
import java.util.*

/**
 * Created by Pasenchuk Victor on 28/08/2017
 */

data class S(val i: Int, val s: String)

data class S1(val i: Int, val s: String, val t: Int?)

val I = Field("i")
val J = Field("j")

fun main(args: Array<String>) {

    val leafDbJdbcHelper = SampleHelper("sample.db", 3)

    val leafDb = leafDbJdbcHelper.leafDb

    leafDb.insertOrUpdate(Tables.TEST.tableName, S(1, "asd"), I).execute()
    leafDb.insertOrUpdate(Tables.TEST.tableName, S(5, "qwe"), I).execute()

    leafDb.partialUpdate(Tables.TEST.tableName, mapOf("i" to 1, "t" to 4), I).execute()

    leafDb.partialUpdate(Tables.TEST.tableName, mapOf("t" to 4), I equal 5).execute()

    System.out.println(leafDb.engineVersion().execute())

    System.out.println(leafDb.select(Tables.TEST.tableName).findAs(S1::class.java).execute())

    val s = S(1, "1")
    System.out.println(Date())
    for (i in 1..1_000)
        leafDb.insert(Tables.LOAD.tableName, s).execute()

    System.out.println(Date())

}
