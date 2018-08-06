package com.corphish.nightlight.helpers

import eu.chainfire.libsuperuser.Shell

/**
 * Root Utils
 * This must be called inside an async task
 */
object RootUtils {

    /**
     * Gets root access.
     * @return A boolean indicating whether root access is available or not.
     */
    val rootAccess: Boolean
        get() = Shell.SU.available()

    /**
     * Writes text to a file as root.
     * It overwrites the contents of file, it does not append it.
     * @param textToBeWritten The text that is to be written.
     * @param file The file in which the text would be written.
     */
    fun writeToFile(textToBeWritten: String, file: String): Boolean {
        val command = "echo \"$textToBeWritten\" > $file"
        Shell.SU.run(command)
        return Shell.SU.available()
    }

    /**
     * Reads contents of file
     * @param file File whose contents is to be read
     * @return Contents of file
     */
    fun readContents(file: String): List<String>? {
        return Shell.SU.run("cat $file")
    }

    fun doesFileExist(file: String): Boolean {
        val output = Shell.SU.run("ls $file")
        return !output.isEmpty() && output[0] == file
    }

    /**
     * Reads one line for given file (path)
     * @param file Path of file to read
     * @return Contents of file. If It has multiple lines, first line is returned
     */
    fun readOneLine(file: String): String {
        val output = readContents(file)
        return if (output == null || output.isEmpty()) "" else output[0]
    }
}