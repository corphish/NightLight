package com.corphish.nightlight.engine

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.corphish.nightlight.data.Constants
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.AutomationRoutine.Companion.resolved
import com.corphish.nightlight.helpers.AlarmUtils
import com.corphish.nightlight.helpers.TimeUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class to manage automation routines.
 */
object AutomationRoutineManager {
    // Shared preference key to store data.
    private val _prefKey = "automationRoutines"

    /* List to store the routines.
     * The order totally depends on the timings of the routines, as there isn't any
     * specifically.
     * But we do not allow overlapping routines.
     */
    private var _automationRoutineList: MutableList<AutomationRoutine> = ArrayList()
    val automationRoutineList: List<AutomationRoutine>
        get() = _automationRoutineList

    /**
     * Loads the persisted routines.
     *
     * @param context [Context].
     */
    fun loadRoutines(context: Context) {
        // Load the routines.
        val routines = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet(_prefKey, TreeSet())

        if (routines != null) {
            // Transform the set into list
            _automationRoutineList = routines
                    .filterNotNull()
                    .map { AutomationRoutine.fromString(it) }
                    .toMutableList()
        }
    }

    /**
     * Stores the local routines.
     * It also schedules alarms.
     *
     * @param context [Context].
     */
    fun persistRoutines(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit {
                    putStringSet(
                            _prefKey,
                            _automationRoutineList.map {
                                it.toString()
                            }.toSet()
                    )
                }

        scheduleAlarms(context)
    }

    /**
     * Checks if a target automation routine overlaps with any of the existing.
     *
     * @param context [Context]
     * @param automationRoutine [AutomationRoutine] to check.
     * @param indexToIgnore Index of automation routine list ot ignore. Useful while updating.
     * @return Boolean. True if the target automation routine overlaps, false
     *         otherwise.
     */
    fun doesOverlap(context: Context, automationRoutine: AutomationRoutine, indexToIgnore: Int = -1): Boolean {
        for ((i, x) in _automationRoutineList.withIndex()) {
            // Ignore the said index.
            if (i == indexToIgnore) {
                continue
            }

            // Check if start time overlaps.
            if (TimeUtils.isInRange(
                            startTime = x.startTime.resolved(context),
                            endTime = x.endTime.resolved(context),
                            targetTime = automationRoutine.startTime.resolved(context),
                            startExclusive = true,
                            endExclusive = true)
            ) {
                return true
            }

            // Check if end time overlaps.
            if (TimeUtils.isInRange(
                            startTime = x.startTime.resolved(context),
                            endTime = x.endTime.resolved(context),
                            targetTime = automationRoutine.endTime.resolved(context),
                            startExclusive = true,
                            endExclusive = true)) {
                return true
            }
        }

        return false
    }

    /**
     * Adds a routine to the list.
     *
     * @param context [Context]
     * @param automationRoutine [AutomationRoutine] to add.
     * @return [Boolean]. True if the addition was successful, false otherwise.
     *         Success depends on whether the automation routine overlaps with
     *         others or not.
     */
    fun addRoutine(context: Context, automationRoutine: AutomationRoutine): Boolean {
        if (doesOverlap(context, automationRoutine)) {
            return false
        }

        _automationRoutineList.add(automationRoutine)

        return true
    }

    /**
     * Deletes routine at given index.
     *
     * @param idx Index.
     */
    fun deleteRoutineAt(idx: Int) {
        _automationRoutineList.removeAt(idx)
    }

    /**
     * Updates a routine int the list.
     *
     * @param context [Context]
     * @param idx Index of list to update.
     * @param automationRoutine [AutomationRoutine] to update.
     * @return [Boolean]. True if the update was successful, false otherwise.
     *         Success depends on whether the automation routine overlaps with
     *         others or not.
     */
    fun updateRoutineAt(context: Context, idx: Int, automationRoutine: AutomationRoutine): Boolean {
        if (doesOverlap(context, automationRoutine, idx)) {
            return false
        }

        _automationRoutineList[idx] = automationRoutine
        return true
    }

    /**
     * Gets current routine for the time.
     * Returns null if no routine is available for present schedule.
     */
    fun getCurrentRoutine(context: Context): AutomationRoutine? {
        for (x in _automationRoutineList) {
            if (TimeUtils.isInRange(x.startTime.resolved(context), x.endTime.resolved(context))) {
                return x
            }
        }

        return null
    }

    /**
     * Returns the upcoming routine.
     */
    fun getUpcomingRoutine(context: Context): AutomationRoutine? {
        if (_automationRoutineList.isEmpty()) {
            return null
        }

        // First we collect the start times of all the routines and sort them.
        val startTimes = _automationRoutineList.sortedBy { it.startTime.resolved(context) }

        // Get current time.
        val currentTimeRaw = TimeUtils.currentTimeAsHourAndMinutes
        val currentTime = String.format("%02d:%02d", currentTimeRaw[0], currentTimeRaw[1])

        // Binary search to find the upcoming routine.
        // Usually we expect that the current time will not be found in the collected times.
        // In such cases, we invert the return value and increment it by one and then return
        // the resulting routine.
        // If the current time is directly found in the collected times, we return the
        // routine with that time instead.
        val idx = startTimes.binarySearchBy(currentTime) { it.startTime }
        return if (idx >= 0) {
            startTimes[idx]
        } else {
            var index = -idx - 1
            if (index >= _automationRoutineList.size) index = _automationRoutineList.size - 1
            startTimes[index]
        }
    }

    /**
     * Schedules alarms.
     */
    fun scheduleAlarms(context: Context) {
        // Bail out if there are no routines
        if (_automationRoutineList.isEmpty()) {
            applyDefaultBehavior(context)
            return
        }

        // Get current routine.
        val currentRoutine = getCurrentRoutine(context)

        if (currentRoutine == null) {
            // Apply default behavior
            applyDefaultBehavior(context)

            // Set alarm for upcoming ones.
            val upcoming = getUpcomingRoutine(context)
            if (upcoming != null) {
                AlarmUtils.setAlarmAbsolute(context, upcoming.startTime.resolved(context))
            }
        } else {
            // We have a current routine.
            // Schedule an alarm now.
            AlarmUtils.setAlarmRelative(context, 0)
        }
    }

    /**
     * Applies default behavior set by the user.
     */
    fun applyDefaultBehavior(context: Context) {
        val defaultBehavior = AutomationRoutine.whenOutside(context)
        if (!defaultBehavior.switchState) {
            Core.applyNightModeAsync(false, context)
        } else {
            val rgb = defaultBehavior.rgbFrom
            if (defaultBehavior.fadeBehavior.settingType == Constants.NL_SETTING_MODE_TEMP) {
                Core.applyNightModeAsync(true, context, rgb[0])
            } else {
                Core.applyNightModeAsync(true, context, rgb[0], rgb[1], rgb[2])
            }
        }
    }
}