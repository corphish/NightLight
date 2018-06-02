package com.corphish.nightlight.helpers

/**
 * Created by avinabadalal on 03/03/18.
 * String helper class
 */

object StringUtils {
    /**
     * Coverts int array represented as string (using Arrays.toString()) back to int array
     * @param str Int array represented as strings
     * @return Resultant int array
     */
    fun stringToIntArray(string: String): IntArray {
        var str = string
        // Strip of the leading and trailing [/]
        str = str.trim { it <= ' ' }.substring(1, str.length - 1)

        val parts = str.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val arr = IntArray(parts.size)
        var i = 0

        for (part in parts) arr[i++] = Integer.parseInt(part.trim { it <= ' ' })

        return arr
    }
}
