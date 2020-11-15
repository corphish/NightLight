package com.corphish.nightlight.engine

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.corphish.nightlight.engine.models.AutomationRoutine
import com.corphish.nightlight.engine.models.AutomationRoutine.Companion.resolved
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
            if (TimeUtils.determineWhetherNLShouldBeOnOrNot(
                            startTime = x.startTime.resolved(context),
                            endTime = x.endTime.resolved(context),
                            targetTime = automationRoutine.startTime.resolved(context))
            ) {
                return true
            }

            // Check if end time overlaps.
            if (TimeUtils.determineWhetherNLShouldBeOnOrNot(
                            startTime = x.startTime.resolved(context),
                            endTime = x.endTime.resolved(context),
                            targetTime = automationRoutine.endTime.resolved(context))
            ) {
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
     */
    fun getCurrentRoutine(context: Context): AutomationRoutine {
        for (x in _automationRoutineList) {
            if (TimeUtils.determineWhetherNLShouldBeOnOrNot(x.startTime.resolved(context), x.endTime.resolved(context))) {
                return x
            }
        }

        // TODO: Build automation routine for behavior outside schedule
        return AutomationRoutine()
    }
}