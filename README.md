# Night Light
[![Build Status](https://travis-ci.org/corphish/NightLight.svg?branch=master)](https://travis-ci.org/corphish/NightLight)

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_60.png)](https://play.google.com/store/apps/details?id=com.corphish.nightlight.generic)

Night light uses KCAL to adjust blue light intensity of the display colors, so that viewing the screen at dark becomes pleasant for the eyes, and help you fall asleep faster (this is what science have proven so...).

### What is KCAL?
KCAL is a display driver tuning feature for Qualcomm devices, written by `savoca`. Although this feature is most likely not shipped in the stock kernels of various custom ROMs, it should be available in the custom kernels available for your device, provided that your device runs on any 64-bit Qualcomm processor. To see the full list of supported qcom SoCs, visit [this thread](https://forum.xda-developers.com/android/software-hacking/dev-kcal-advanced-color-control-t3032080).

### Why on earth did I make it?
I flashed a LineageOS oreo build on my device, but there was no night light feature available on the ROM. The build itself was early so there was no LiveDisplay, and the Android Oreo night light feature was not their as it is Pixel specific. Luckily, the kernel shipped with the build had KCAL, so I decided to harness this feature. But I had to everytime use a kernel manager app, and had to adjust the intensity, hence I decided to make an app that would make this easier for me.

### Features
* Control blue light filter intensity to your liking.
* Control green light filter to add reddish tint.
* Easy one touch toggles, with a single slider to tweak blue light intensity.
* Quick Setting tile for easy toggling on/off night light anywhere.
* Automatic switching on/off night light at user specifed timings.
* Supports sunset/sunrise timings.

### Requirements
* Kernel supporting KCAL.
* Root access.

### Permissions
* Location - Needed to determine sunset/sunrise time for your current location.
* Internet - Internet connection is needed to determine coarse location when fine location is not available.
