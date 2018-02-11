package com.corphish.nightlight.engine.presets;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by avinabadalal on 12/02/18.
 * Preset database
 */



@Database(entities = {Preset.class}, version = 1)
public abstract class PresetDatabase extends RoomDatabase {
    public abstract PresetDao getPresetDao();
}
