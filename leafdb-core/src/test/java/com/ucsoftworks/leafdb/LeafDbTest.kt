package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.dsl.Field
import com.ucsoftworks.leafdb.dsl.AggregateFunction
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import com.ucsoftworks.leafdb.wrapper.ILeafDbReadableDatabase
import com.ucsoftworks.leafdb.wrapper.ILeafDbWritableDatabase
import org.junit.Test
import pretty_tests.expectedBe
import java.util.*

/**
 * Created by Pasenchuk Victor on 26/08/2017
 */
class LeafDbTest {

    val leafDb = LeafDb(object : ILeafDbProvider {
        override val listQueries = mutableListOf<ILeafDbProvider.ListQuery>()

        override val readableDb: ILeafDbReadableDatabase
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val writableDb: ILeafDbWritableDatabase
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    })

    @Test
    fun count() {
        leafDb.count("t").leafDbStringQuery.query expectedBe "SELECT COUNT(*) FROM t;"

        leafDb.count("t", Field("a") greaterThan 5).leafDbStringQuery.query expectedBe "SELECT COUNT(*) FROM t WHERE json_extract(doc, '\$.a') > 5;"

    }

    @Test
    fun aggregate() {
        leafDb.aggregate("t", Field("a"), AggregateFunction.AVG).leafDbStringQuery.query expectedBe "SELECT AVG(f) FROM (SELECT json_extract(doc, '\$.a') AS f FROM t);"

        leafDb.aggregate("t", Field("a"), AggregateFunction.AVG, Field("a") greaterThan 5).leafDbStringQuery.query expectedBe "SELECT AVG(f) FROM (SELECT json_extract(doc, '\$.a') AS f FROM t WHERE json_extract(doc, '\$.a') > 5);"

        leafDb.aggregate("t", Field("a"), AggregateFunction.AVG, Date::class.java, Field("a") greaterThan 5).leafDbStringQuery.query expectedBe "SELECT AVG(f) FROM (SELECT json_extract(doc, '\$.a') AS f FROM t WHERE json_extract(doc, '\$.a') > 5);"

    }

}
