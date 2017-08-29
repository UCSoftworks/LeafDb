@file:Suppress("IllegalIdentifier")
package pretty_tests

import org.junit.Assert


/**
 * Created by pasencukviktor on 02/06/2017
 */

infix fun <T> T.shouldBe(expected: T): Pair<T, T> = Pair(this, expected)

infix fun <T> Pair<T, T>.`if wrong print`(message: String)
        = Assert.assertEquals(message, second, first)

infix fun CharSequence?.`should be empty, if not print`(message: String)
        = Assert.assertTrue(message, isNullOrEmpty())

infix fun CharSequence?.`should NOT be empty, if not print`(message: String)
        = Assert.assertFalse(message, isNullOrEmpty())

infix fun <T> T?.`should NOT be null, if not print`(message: String)
        = Assert.assertNotNull(message, this)

infix fun <T> T?.`should be null, if not print`(message: String)
        = Assert.assertNull(message, this)

infix fun Boolean.`should be TRUE, if not print`(message: String)
        = Assert.assertTrue(message, this)

infix fun Boolean.`should be FALSE, if not print`(message: String)
        = Assert.assertFalse(message, this)
