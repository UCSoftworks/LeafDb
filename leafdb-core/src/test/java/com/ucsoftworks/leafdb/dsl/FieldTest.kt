@file:Suppress("IllegalIdentifier")

package com.ucsoftworks.leafdb.dsl

import org.junit.Test
import pretty_tests.`if wrong print`
import pretty_tests.shouldBe

/**
 * Created by Pasenchuk Victor on 11/08/2017
 */
class FieldTest {

    @Test
    fun testSingleConstructor() {
        val field = Field("a")

        field.field shouldBe "json_extract(doc, '$.a')" `if wrong print` "wrong field data"
    }

    @Test
    fun testPathConstructor() {
        var field = Field("a", "b", "c")

        field.field shouldBe "json_extract(doc, '$.a.b.c')" `if wrong print` "wrong field data"

        field = Field()

        field.field shouldBe "json_extract(doc, '$')" `if wrong print` "wrong field data"
    }

    @Test
    fun testFieldOperatorConstructor() {
        val a = Field("a")
        val b = Field("b")
        val c = Field("c")


        +a shouldBe "+json_extract(doc, '$.a')"
        -a shouldBe "-json_extract(doc, '$.a')"

        (a + b).field shouldBe "(json_extract(doc, '$.a') + json_extract(doc, '$.b'))" `if wrong print` "wrong field data"
        (a - b).field shouldBe "(json_extract(doc, '$.a') - json_extract(doc, '$.b'))" `if wrong print` "wrong field data"
        (b + a).field shouldBe "(json_extract(doc, '$.b') + json_extract(doc, '$.a'))" `if wrong print` "wrong field data"
        (a * b).field shouldBe "(json_extract(doc, '$.a') * json_extract(doc, '$.b'))" `if wrong print` "wrong field data"
        (a / b).field shouldBe "(json_extract(doc, '$.a') / json_extract(doc, '$.b'))" `if wrong print` "wrong field data"
        (a % b).field shouldBe "(json_extract(doc, '$.a') % json_extract(doc, '$.b'))" `if wrong print` "wrong field data"

        (a + b * c).field shouldBe "(json_extract(doc, '$.a') + (json_extract(doc, '$.b') * json_extract(doc, '$.c')))" `if wrong print` "wrong field data"

    }

    @Test
    fun testFieldNumberConstructor() {
        val a = Field("a")
        val b = Field("b")
        val c = Field("c")


        (a + 1).field shouldBe "(json_extract(doc, '$.a') + 1)" `if wrong print` "wrong field data"
        (a - 1).field shouldBe "(json_extract(doc, '$.a') - 1)" `if wrong print` "wrong field data"
        (a * b).field shouldBe "(json_extract(doc, '$.a') * json_extract(doc, '$.b'))" `if wrong print` "wrong field data"
        (a / 1.5).field shouldBe "(json_extract(doc, '$.a') / 1.5)" `if wrong print` "wrong field data"
        (a % 1f).field shouldBe "(json_extract(doc, '$.a') % 1.0)" `if wrong print` "wrong field data"

        (1 / a).field shouldBe "(1 / json_extract(doc, '$.a'))" `if wrong print` "wrong field data"

        (a + b * 2f).field shouldBe "(json_extract(doc, '$.a') + (json_extract(doc, '$.b') * 2.0))" `if wrong print` "wrong field data"

    }

}
