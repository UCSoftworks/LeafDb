@file:Suppress("IllegalIdentifier")

package com.ucsoftworks.leafdb.serializer

import pretty_tests.`if wrong print`
import pretty_tests.shouldBe

/**
 * Created by Pasenchuk Victor on 07/08/2017
 */
class JsonMergerTest {

    data class A(val i: Int, val s: String)

    data class B(val i: Int, val s: String, val f: Double)

    data class C(val i: Int, val s: String, val a: A, val l: List<String>, val n: Any)

    val gson = com.google.gson.Gson()

    val jsonMerger = Serializer()

    @org.junit.Test
    fun mergePlainTest() {
        val mapOf = mapOf("i" to 1, "f" to 4.5)
        val json = gson.toJson(mapOf)

        val merge = jsonMerger.merge(gson.toJson(A(5, "asd")), json)


        val b = jsonMerger.getObjectFromJson(merge!!, B::class.java)

        System.out.println(merge)


        b.f shouldBe 4.5 `if wrong print` "Incorrect parsing"
    }

    @org.junit.Test
    fun mergeTreeTest() {
        val old = gson.toJson(mapOf("i" to 1, "f" to 4.5, "n" to "sd", "n1" to null, "s" to "asd", "a" to mapOf("i" to 1, "f" to 4.5), "l" to listOf(mapOf("i" to 1, "f" to 4.5))))
        val new = mapOf("i" to 2, "f" to 5.5, "n" to null, "n1" to null, "n2" to null, "s" to "qwe", "a" to mapOf("i" to 1, "s" to "4"), "l" to listOf("a", "b"))

        val merge = jsonMerger.merge(gson.toJson(new), old)


        val c = jsonMerger.getObjectFromJson(merge!!, C::class.java)

        System.out.println(merge)
        System.out.println(gson.toJson("{\"a\":[1,2,3]}"))


        c.i shouldBe 2 `if wrong print` "Incorrect parsing"
    }

    fun <T : Any> compare(json: String, t: T) {
//        gson.
    }

    @org.junit.Test
    fun mergeJson() {

    }

    @org.junit.Test
    fun hasSameType() {

    }

}
