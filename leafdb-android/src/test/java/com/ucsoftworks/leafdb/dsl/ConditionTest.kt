@file:Suppress("IllegalIdentifier")

package com.ucsoftworks.leafdb.dsl

import org.junit.Test
import pretty_tests.`if wrong print`
import pretty_tests.shouldBe
import java.util.*

/**
 * Created by Pasenchuk Victor on 14/08/2017
 */
class ConditionTest {

    val A = Field("a")
    val B = Field("b")
    val C = Field("c")
    val D = Field("d")

    @Test
    fun testConditions() {
        (A greaterThan 0).condition shouldBe
                "json_extract(doc, '$.a') > 0" `if wrong print` "wrong condition"

        (D between Date(1234)..Date(12345)).condition shouldBe
                "json_extract(doc, '$.d') BETWEEN 1234 AND 12345" `if wrong print` "wrong condition"

        (A + C equal 15 and !(B equal "Vasya") or (C notEqual 21)).condition shouldBe
                "(((json_extract(doc, '$.a') + json_extract(doc, '$.c')) = 15) AND (NOT (json_extract(doc, '$.b') = 'Vasya'))) OR (json_extract(doc, '$.c') <> 21)" `if wrong print` "wrong condition"

        (Field("child", "a") % 5 lessThanOrEqual 2f).condition shouldBe
                "(json_extract(doc, '$.child.a') % 5) <= 2.0" `if wrong print` "wrong condition"

        (B like "__asd%").condition shouldBe
                "json_extract(doc, '$.b') LIKE '__asd%'" `if wrong print` "wrong condition"

        (B equal  "qwe'; drop table A;").condition shouldBe
                "json_extract(doc, '$.b') = 'qwe''; drop table A;'" `if wrong print` "wrong condition"
    }
}
