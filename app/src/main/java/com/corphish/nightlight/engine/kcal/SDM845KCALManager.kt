package com.corphish.nightlight.engine.kcal

/*
 * KCAL implementation for newer kernels (starting with v4.4)\
 * This implementation was first seen in kernels in devices with SDM845, hence the name.
 */
class SDM845KCALManager : KCALAbstraction {
    /**
     * A function to determine whether the implementation is supported by device
     */
    override fun isSupported(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * A function to turn on KCAL
     */
    override fun turnOn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * A function to adjust KCAL colors
     */
    override fun setColors(red: Int, green: Int, blue: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * A function get current KCAL color readings
     */
    override fun getColorReadings(): IntArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}