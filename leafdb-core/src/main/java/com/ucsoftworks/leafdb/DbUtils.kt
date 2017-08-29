package com.ucsoftworks.leafdb

import com.ucsoftworks.leafdb.wrapper.ILeafDbCursor
import java.util.*

/**
 * Created by Pasenchuk Victor on 19/08/2017
 */
internal fun appendEscapedSQLString(sb: StringBuilder, sqlString: String) {
    sb.append('\'')
    if (sqlString.indexOf('\'') != -1) {
        val length = sqlString.length
        for (i in 0..length - 1) {
            val c = sqlString[i]
            if (c == '\'') {
                sb.append('\'')
            }
            sb.append(c)
        }
    } else
        sb.append(sqlString)
    sb.append('\'')
}

/**
 * SQL-escape a string.
 */
internal fun sqlEscapeString(value: String): String {
    val escaper = StringBuilder()

    appendEscapedSQLString(escaper, value)

    return escaper.toString()
}

internal val Any?.sqlValue: String
    get() = when (this) {
        null -> "NULL"
        is Number -> this.toString()
        is Date -> this.time.toString()
        else -> sqlEscapeString(this.toString())
    }


internal fun ILeafDbCursor?.collectStrings(position: Int = 1): List<Pair<Long, String>> {
    val strings = mutableListOf<Pair<Long, String>>()

    try {
        if (this != null && !this.isClosed && !this.empty)
            do {
                strings.add(Pair(this.getLong(1), this.getString(position)))
            } while (this.moveToNext())
    } finally {
        if (this != null && !this.isClosed) {
            this.close()
        }
    }

    return strings
}