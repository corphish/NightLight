package com.corphish.nightlight.engine.presets;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by avinabadalal on 12/02/18.
 * Preset data access object
 */

@Dao
public interface PresetDao {
    /**
     * Gets all user created and default preset
     * @return All presets
     */
    @Query("SELECT * from presets")
    List<Preset> getAllPresets();

    /**
     * Gets all preset with automation enabled
     * @return Presets with automation enabled
     */
    @Query("SELECT * from presets WHERE automation_enabled IS \"true\"")
    List<Preset> getAllPresetsWithAutomationEnabled();

    /**
     * Gets all user created presets
     * @return Presets created by user
     */
    @Query("SELECT * from presets where type IS 1")
    List<Preset> getAllUserPresets();

    /**
     * Gets number of all default presets
     * @return Number of default presets
     */
    @Query("SELECT count(*) from presets where type IS 0")
    int getDefaultPresetCount();

    /**
     * Inserts preset into persistent store
     * @param presets Presets to be saved
     */
    @Insert
    void insert(Preset... presets);

    /**
     * Deletes the given preset
     * @param preset Preset to be deleted
     */
    @Delete
    void delete(Preset preset);
}
