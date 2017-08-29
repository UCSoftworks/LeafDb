package com.ucsoftworks.leafdb.serializer

import org.junit.Test

import org.junit.Assert.*
import pretty_tests.`if wrong print`
import pretty_tests.expectedBe
import pretty_tests.shouldBe

/**
 * Created by Pasenchuk Victor on 26/08/2017
 */
class SerializerTest {
    val serializer = Serializer()

    @Test
    fun getObjectFromJson() {
        serializer.getObjectFromJson("asd", String::class.java) expectedBe "asd"

    }

    @Test
    fun getJsonFromObject() {
        serializer.getJsonFromObject(true) shouldBe "true" `if wrong print` "wrong serialization"
        serializer.getJsonFromObject(1) shouldBe "1" `if wrong print` "wrong serialization"
        serializer.getJsonFromObject(1f) shouldBe "1.0" `if wrong print` "wrong serialization"
        serializer.getJsonFromObject("{}") shouldBe "\"{}\"" `if wrong print` "wrong serialization"
    }

}
