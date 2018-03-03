package com.corphish.nightlight.helpers;

/**
 * Created by avinabadalal on 03/03/18.
 * String helper class
 */

public class StringUtils {
    /**
     * Coverts int array represented as string (using Arrays.toString()) back to int array
     * @param str Int array represented as strings
     * @return Resultant int array
     */
    public static int[] stringToIntArray(String str) {
        // Strip of the leading and trailing [/]
        str = str.trim().substring(1, str.length() - 1);

        String parts[] = str.split(",");

        int arr[] = new int[parts.length], i = 0;

        for (String part: parts) arr[i++] = Integer.parseInt(part.trim());

        return arr;
    }
}
