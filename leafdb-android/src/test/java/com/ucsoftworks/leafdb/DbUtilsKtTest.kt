@file:Suppress("IllegalIdentifier")

package com.ucsoftworks.leafdb

import org.junit.Test
import pretty_tests.`if wrong print`
import pretty_tests.shouldBe

/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
class DbUtilsKtTest {
    @Test
    fun getSqlValue() {
        "'".sqlValue shouldBe "''''" `if wrong print` "wrong value"

        "\\'".sqlValue shouldBe "'\\'''" `if wrong print` "wrong value"
    }

}