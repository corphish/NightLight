package com.corphish.nightlight.Helpers;

import eu.chainfire.libsuperuser.Shell;

/**
 * Root Utils
 * This must be called inside an async task
 */
public class RootUtils {

    public static boolean getRootAccess() {
        return Shell.SU.available();
    }

    public static void writeToFile(String textToBeWritten, String file) {
        String command = "echo \"" + textToBeWritten + "\" > " + file;
        Shell.SU.run(command);
    }
}