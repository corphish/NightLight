package com.corphish.nightlight.extensions

/*
 * Extensions for boolean arrays
 */
fun BooleanArray.toFormattedString(): String {
    var res = ""
    for (i in this)
        res += if (i) "T" else "F"

    return res
}