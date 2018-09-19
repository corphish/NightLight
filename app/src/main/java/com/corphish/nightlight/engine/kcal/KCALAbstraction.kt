package com.corphish.nightlight.engine.kcal

/*
 * KCAL Abstraction
 * Objective of this abstraction is to provide a generic way to work with various KCAL implementations
 */
interface KCALAbstraction {
    /**
     * A function to determine whether the implementation is supported by device
     */
    fun isSupported() : Boolean

    /**
     * A function to determine whether KCAL is enabled or not
     */
    fun isEnabled() : Boolean

    /**
     * A function to turn on KCAL
     */
    fun turnOn()

    /**
     * A function to adjust KCAL colors
     */
    fun setColors(red: Int, green: Int, blue: Int)

    /**
     * A function get current KCAL color readings
     */
    fun getColorReadings() : IntArray
}