package com.corphish.nightlight.engine.presets;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by avinabadalal on 12/02/18.
 * Preset model
 */

@Entity(tableName = "presets")
public class Preset {
    /**
     * Name of the preset
     * IMPORTANT - This must be unique
     */
    @PrimaryKey
    @NonNull
    private String presetName = "";

    /**
     * Preset type
     * They can be either default or user generated
     */
    public static final int PRESET_TYPE_DEFAULT     = 0;
    public static final int PRESET_TYPE_USER        = 1;

    /**
     * Type of preset
     */
    @ColumnInfo(name = "type")
    private int type;

    /**
     * Filter intensities
     */
    @ColumnInfo(name = "blue_light")
    private int blueLightIntensity;

    @ColumnInfo(name = "green_light")
    private int greenLightIntensity;

    /**
     * Presets will also support automation
     */
    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "end_time")
    private String endTime;

    @ColumnInfo(name = "automation_enabled")
    private boolean automationEnabled;

    /**
     * Returns whether automation is enabled or not
     * @return A boolean indicating whether automation is enabled or not
     */
    public boolean isAutomationEnabled() {
        return automationEnabled;
    }

    /**
     * Returns blue light intensity for this preset
     * @return Blue light intensity
     */
    public int getBlueLightIntensity() {
        return blueLightIntensity;
    }

    /**
     * Returns green light intensity for this preset
     * @return Green light intensity
     */
    public int getGreenLightIntensity() {
        return greenLightIntensity;
    }

    /**
     * Returns end time for this automation
     * @return End time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Returns start time for this automation
     * @return Start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Returns name of this preset
     * @return Preset time
     */
    public String getPresetName() {
        return presetName;
    }

    /**
     * Returns preset type
     * @return Preset type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets automation enabled parameter
     * @param automationEnabled Whether automation must be enabled on not
     */
    public void setAutomationEnabled(boolean automationEnabled) {
        this.automationEnabled = automationEnabled;
    }

    /**
     * Sets blue light intensity
     * @param blueLightIntensity Blue light intensity
     */
    public void setBlueLightIntensity(int blueLightIntensity) {
        this.blueLightIntensity = blueLightIntensity;
    }

    /**
     * Sets end time
     * @param endTime End time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets green light intensity
     * @param greenLightIntensity Green light intensity
     */
    public void setGreenLightIntensity(int greenLightIntensity) {
        this.greenLightIntensity = greenLightIntensity;
    }

    /**
     * Sets preset name
     * @param presetName Preset name
     */
    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    /**
     * Sets start time
     * @param startTime Start time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets preset type
     * @param type Type
     */
    public void setType(int type) {
        this.type = type;
    }

    public void dump() {
        String TAG = "NL_Preset";
        Log.i(TAG, "Name - " + getPresetName());
        Log.i(TAG, "Blue - " + getBlueLightIntensity());
        Log.i(TAG, "Green - " + getGreenLightIntensity());
        Log.i(TAG, "Start - " + getStartTime());
        Log.i(TAG, "End - " + getEndTime());
        Log.i(TAG, "Type - " + getType());
        Log.i(TAG, "Automated - " + isAutomationEnabled());
    }
}
