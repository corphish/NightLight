package com.corphish.nightlight.helpers;

/**
 * Created by avinabadalal on 12/02/18.
 * Color Temperature Util helper class to convert temperature to RGB
 * Based on http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
 */

public class ColorTemperatureUtil {

    /**
     * Converts color temperature to RGB
     * @param colorTemperature Color Temperature in kelvin
     * @return RGB color int
     */
    public static int[] colorTemperatureToIntRGB(int colorTemperature) {
        // Max permissible range of temperature (1000-10000K)
        if (colorTemperature < 1000 || colorTemperature > 10000) return new int[] {255, 255, 255};

        int red, green, blue;

        // We are not interested in last 2 digits
        colorTemperature /= 100;

        if (colorTemperature < 67) {
            red = 255;
            green = (int)(99.4708025861f * Math.log(colorTemperature) - 161.1195681661f);

            if (colorTemperature < 20) {
                blue = 0;
            } else {
                blue = (int)(138.5177312231 * Math.log(colorTemperature - 10) - 305.0447927307f);
            }
        } else {
            red = (int)(329.698727446f * Math.pow(colorTemperature - 60, -0.1332047592));
            green = (int)(288.1221695283f * Math.pow(colorTemperature - 60, -0.0755148492));
            blue = 255;
        }

        return new int[] {
                fixColorBounds(red),
                fixColorBounds(green),
                fixColorBounds(blue),
        };
    }

    private static int fixColorBounds(int color) {
        return color < 0 ? 0 : (color > 255 ? 255 : color);
    }
}
