package com.corphish.nightlight.engine;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.corphish.nightlight.engine.presets.Preset;
import com.corphish.nightlight.engine.presets.PresetDatabase;

import java.util.List;

import static com.corphish.nightlight.engine.presets.Preset.PRESET_TYPE_DEFAULT;

/**
 * Created by avinabadalal on 12/02/18.
 * Preset Manager provides a way to do various operations on Preset like creating, modifying etc.
 * Presets are a collection of settings of various parameters, which the user can switch to with one click
 */

public class PresetManager {

    /*
     * Singleton implementation
     */
    private static PresetManager ourInstance = new PresetManager();

    public static PresetManager getInstance() {
        return ourInstance;
    }

    private PresetManager() {}

    /**
     * Database operations interface to wrap up a database operation which will be executed inside AsyncTask/Thread
     */
    private interface DatabaseOperations {
        /**
         * Operation to be execute
         */
        void execute();
    }

    /**
     * Preset database
     */
    private PresetDatabase presetDatabase;

    /**
     * Initializes the database
     * @param context Context is needed for databse
     */
    public void initialize(Context context) {
        presetDatabase = Room.databaseBuilder(context, PresetDatabase.class, "presets").build();
    }

    public void test() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Preset preset = new Preset();
                preset.setPresetName("Test");
                preset.setAutomationEnabled(true);
                preset.setBlueLightIntensity(20);
                preset.setGreenLightIntensity(100);
                preset.setEndTime("06:00");
                preset.setStartTime("00:00");
                preset.setType(PRESET_TYPE_DEFAULT);

                try {
                    presetDatabase.getPresetDao().insert(preset);
                } catch (SQLiteConstraintException e) {
                    Log.i("NL_PresetManager","Tried to insert field with duplicate unique key?");
                }

                List<Preset> presets = presetDatabase.getPresetDao().getAllPresets();
                for (Preset p: presets) {
                    p.dump();
                    if (p == preset) Log.i("NL_PresetManager", "Object points to same object");
                }
            }
        }).start();
    }

    private class PresetThread extends Thread {
        private DatabaseOperations databaseOperations;

        public PresetThread withOperations(DatabaseOperations databaseOperations) {
            this.databaseOperations = databaseOperations;

            return this;
        }

        @Override
        public void run() {
            databaseOperations.execute();
        }
    }
}
