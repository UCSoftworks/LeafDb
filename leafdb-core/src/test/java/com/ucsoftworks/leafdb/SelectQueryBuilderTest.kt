@file:Suppress("IllegalIdentifier")

package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.dsl.Field
import com.ucsoftworks.leafdb.dsl.div
import com.ucsoftworks.leafdb.dsl.SortOrder
import com.ucsoftworks.leafdb.wrapper.ILeafDbProvider
import com.ucsoftworks.leafdb.wrapper.ILeafDbReadableDatabase
import com.ucsoftworks.leafdb.wrapper.ILeafDbWritableDatabase
import org.junit.Test
import pretty_tests.`if wrong print`
import pretty_tests.shouldBe
import querying.SelectQueryBuilder

/**
 * Created by Pasenchuk Victor on 14/08/2017
 */
class SelectQueryBuilderTest {
    internal val leafDbProvider = object : ILeafDbProvider {
        override val listQueries = mutableListOf<ILeafDbProvider.ListQuery>()

        override val readableDb: ILeafDbReadableDatabase
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val writableDb: ILeafDbWritableDatabase
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    }

    @Test
    fun testSqlQueries() {
        var builder = SelectQueryBuilder("table", leafDbProvider)

        builder.createSelectSqlString() shouldBe
                "SELECT json_extract(doc, '$') FROM table;" `if wrong print` "wrong select SQL"

        builder = SelectQueryBuilder("table", leafDbProvider)
                .distinct()
                .field(Field("c"))
                .where(Field("a") greaterThan 5)
                .orderBy(Field("b"), SortOrder.DESC)
                .limit(10)
                .offset(15)


        builder.createSelectSqlString() shouldBe
                "SELECT DISTINCT json_extract(doc, '$.c') FROM table WHERE json_extract(doc, '$.a') > 5 ORDER BY json_extract(doc, '$.b') DESC LIMIT 10 OFFSET 15;" `if wrong print` "wrong select SQL"

        builder = SelectQueryBuilder("table", leafDbProvider).field(1 / Field("a"))

        builder.createSelectSqlString() shouldBe
                "SELECT (1 / json_extract(doc, '$.a')) FROM table;"

    }
}
