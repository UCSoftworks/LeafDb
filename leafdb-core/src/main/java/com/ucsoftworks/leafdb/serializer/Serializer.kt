package com.ucsoftworks.leafdb.serializer

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.util.*


/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
class Serializer {

    private val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, JsonDeserializer<Date> { json, typeOfT, context -> Date(json.asJsonPrimitive.asLong) })
            .registerTypeAdapter(Date::class.java, JsonSerializer<Date> { date, typeOfT, context -> JsonPrimitive(date.time) })
            .serializeNulls()
            .create()

    fun <T> getObjectFromJson(json: String?, clazz: Class<T>): T =
            gson.fromJson(json, TypeToken.get(clazz).type)

    fun getJsonFromObject(any: Any): String = gson.toJson(any)


    internal fun merge(new: String, old: String, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION): String {
        val newObject = gson.fromJson(new, JsonObject::class.java)
        val oldObject = gson.fromJson(old, JsonObject::class.java)

        mergeJson(newObject, oldObject)

        return gson.toJson(oldObject)
    }

    private fun mergeJson(new: JsonObject, old: JsonObject, nullMergeStrategy: NullMergeStrategy = NullMergeStrategy.TAKE_DESTINATION) {
        val newEntries = new.entrySet().associateBy({ it.key }, { it.value })
        val oldEntries = old.entrySet().associateBy({ it.key }, { it.value })

        for ((key, value) in newEntries) {
            if (!oldEntries.containsKey(key))
                old.add(key, value)
            else {
                val oldValue = oldEntries[key]
                if (value.hasSameType(oldValue) && value.isJsonObject)
                    mergeJson(value.asJsonObject, oldValue!!.asJsonObject, nullMergeStrategy)
                else if (!value.hasNullConflict(oldValue))
                    old.add(key, value)
                else {
                    when (nullMergeStrategy) {
                        NullMergeStrategy.TAKE_DESTINATION -> old.add(key, value)
                        NullMergeStrategy.KEEP_SOURCE -> old.add(key, oldValue)
                        NullMergeStrategy.TAKE_NULL -> old.add(key, null)
                        NullMergeStrategy.TAKE_NON_NULL -> old.add(key, if (value.isNull()) oldValue else value)
                    }
                }
            }
        }
    }

    private fun JsonElement?.hasSameType(b: JsonElement?): Boolean {
        if (this == null && b != null)
            return false
        if (this != null && b == null)
            return false

        if (this != null && b != null) {
            if (this.isJsonNull && b.isJsonNull)
                return true
            if (this.isJsonArray && b.isJsonArray)
                return true
            if (this.isJsonObject && b.isJsonObject)
                return true
            if (this.isJsonPrimitive && b.isJsonPrimitive)
                return true
        }
        return false
    }

    private fun JsonElement?.isNull(): Boolean {
        if (this == null)
            return true

        if (this.isJsonNull)
            return true
        return false
    }

    private fun JsonElement?.hasNullConflict(b: JsonElement?): Boolean {
        if (this.isNull() && !b.isNull())
            return true
        if (!this.isNull() && b.isNull())
            return true
        return false
    }

}
