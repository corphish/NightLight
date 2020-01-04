package com.corphish.nightlight.engine.automation

import com.corphish.nightlight.extensions.toFormattedString

data class AutomateAction(
        // Name
        val name: String,

        // Start time
        val startTime: String,

        // End time
        val endTime: String,

        // Days of week to apply
        // Days start from Sunday
        val daysOfWeek: BooleanArray,

        // A profile must be associated with this task
        // Profile with this name will be applied when this action is triggered
        val profileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AutomateAction

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString() =
            "$name;$startTime;$endTime;${daysOfWeek.toFormattedString()};$profileName"
}