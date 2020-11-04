package com.corphish.nightlight.interfaces

import com.corphish.nightlight.engine.models.PickedColorData

/**
 * Color control activity now supports picking colors.
 * However, each mode is handled in separate fragment, which
 * is why we need this callback.
 * Colors are returned using key-value pairs, in the same way it is
 * stored in SharedPreferences.
 */
interface ColorPickerCallback {
    /**
     * Called when user picks a color.
     *
     * @param pickedData Picked color.
     */
    fun onColorPicked(pickedData: PickedColorData)
}