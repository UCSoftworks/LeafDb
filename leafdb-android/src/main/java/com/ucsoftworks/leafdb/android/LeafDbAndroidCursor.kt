package com.ucsoftworks.leafdb.android

import android.database.Cursor
import com.ucsoftworks.leafdb.wrapper.ILeafDbCursor


/**
 * Created by Pasenchuk Victor on 25/08/2017
 */
internal class LeafDbAndroidCursor(private val cursor: Cursor) : ILeafDbCursor {

    override val empty: Boolean
        get() = !cursor.moveToNext()

    override val isClosed: Boolean
        get() = cursor.isClosed

    override fun close() {
        cursor.close()
    }

    override fun moveToNext() =
            cursor.moveToNext()

    override fun getString(index: Int): String
            = cursor.getString(index)

    override fun getLong(index: Int): Long
            = cursor.getLong(index)

    override fun getDouble(index: Int): Double
            = cursor.getDouble(index)

}
