package com.corphish.nightlight.engine.kcal

/*
 * Dummy KCAL manager to handle devices without KCAL support
 */
class DummyKCALManager : KCALAbstraction {
    /**
     * A function to determine whether the implementation is supported by device
     */
    override fun isSupported(): Boolean = false

    /**
     * A function to determine whether KCAL is enabled or not
     */
    override fun isEnabled() : Boolean = false

    /**
     * A function to turn on KCAL
     */
    override fun turnOn() {
        // Not supported
    }

    /**
     * A function to adjust KCAL colors
     */
    override fun setColors(red: Int, green: Int, blue: Int) : Boolean = false

    /**
     * A function get current KCAL color readings
     */
    override fun getColorReadings(): IntArray {
        return intArrayOf(0, 0, 0)
    }

    /**
     * Function to determine whether saturation is supported
     */
    override fun isSaturationSupported(): Boolean {
        return false
    }

    /**
     * Function to get saturation
     */
    override fun getSaturation(): Int {
        return 0
    }

    /**
     * Function to set saturation
     */
    override fun setSaturation(value: Int): Boolean {
        return false
    }

    override fun getImplementationName() = "Dummy KCAL Manager"

    override fun getImplementationSwitchPath() = "Not available"

    override fun getImplementationFilePaths() = "Not available"

    override fun getImplementationFormat() = "Not applicable"
    override fun getSaturationPath() = "Not available"
}