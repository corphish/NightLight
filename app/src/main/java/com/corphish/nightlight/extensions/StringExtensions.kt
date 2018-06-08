package com.corphish.nightlight.extensions

/**
 * This extension converts string separated by delimiter to int array if of appropriate form
 * For example, it should be able to convert String "1,2,3,4,5" to int array
 * In case of error, NumberFormatException is thrown
 */
fun String.toArrayOfInts(delimiter: String) : IntArray {
    val parts = this.replace("[", "").replace("]", "").split(delimiter.toRegex())

    var intArray = intArrayOf()

    for (part in parts) {
        intArray += part.trim().toInt()
    }

    return intArray
}