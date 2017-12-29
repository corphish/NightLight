package com.corphish.nightlight.Helpers;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Root Utils
 * This must be called inside an async task
 */
public class RootUtils {

    /**
     * Gets root access.
     * @return A boolean indicating whether root access is available or not.
     */
    public static boolean getRootAccess() {
        return Shell.SU.available();
    }

    /**
     * Writes text to a file as root.
     * It overwrites the contents of file, it does not append it.
     * @param textToBeWritten The text that is to be written.
     * @param file The file in which the text would be written.
     */
    public static void writeToFile(String textToBeWritten, String file) {
        String command = "echo \"" + textToBeWritten + "\" > " + file;
        Shell.SU.run(command);
    }

    /**
     * Reads contents of file
     * @param file File whose contents is to be read
     * @return Contents of file
     */
    public static List<String> readContents(String file) {
        return Shell.SU.run("cat " + file);
    }

    /**
     * Reads one line for given file (path)
     * @param file Path of file to read
     * @return Contents of file. If It has multiple lines, first line is returned
     */
    public static String readOneLine(String file) {
        return readContents(file).get(0);
    }
}