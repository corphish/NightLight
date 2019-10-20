# Night Light
[![Build Status](https://travis-ci.org/corphish/NightLight.svg?branch=master)](https://travis-ci.org/corphish/NightLight)
[![Donate](https://img.shields.io/badge/donate-paypal-blue.svg)](https://www.paypal.me/corphish)
[![Crowdin](https://d322cqt584bo4o.cloudfront.net/night-light/localized.svg)](https://crowdin.com/project/night-light)

[<img alt='Get it on Google Play'  src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'
height="80"/>](https://play.google.com/store/apps/details?id=com.corphish.nightlight.generic)
[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/packages/com.corphish.nightlight.generic/)

Night light uses KCAL to adjust blue light intensity of the display colors, so that viewing the screen at dark becomes pleasant for the eyes, and help you fall asleep faster (this is what science have proven so...).

### What is KCAL?
KCAL is a display driver tuning feature for Qualcomm devices, written by `savoca`. Although this feature is most likely not shipped in the stock kernels of various custom ROMs, it should be available in the custom kernels available for your device, provided that your device runs on any 64-bit Qualcomm processor. To see the full list of supported qcom SoCs, visit [this thread](https://forum.xda-developers.com/android/software-hacking/dev-kcal-advanced-color-control-t3032080).

### Why on earth did I make it?
I flashed a LineageOS oreo build on my device, but there was no night light feature available on the ROM. The build itself was early so there was no LiveDisplay, and the Android Oreo night light feature was not their as it is Pixel specific. Luckily, the kernel shipped with the build had KCAL, so I decided to harness this feature. But I had to everytime use a kernel manager app, and had to adjust the intensity, hence I decided to make an app that would make this easier for me.


### Features
* Easy to use user interface. Settings are easier to find.
* Uses KCAL to adjust screen RGB colors, hence its efficient and changes are seen everywhere on screen.
* Supports older KCAL implementations as well as newer KCAL implementation for v4.4 kernels.
* Simple color controls for normal users through color temperature control.
* Manual KCAL controls for advanced users.
* Maximum and minimum color intensities. Use minimum intensity at evenings under lighted conditions, and maximum intensity at nights under pitch dark conditions.
* Support for user profiles, which are collections of settings that user can apply with one click.
* Automatic switching on/off night light at user specified timings.
* Supports sunset/sunrise timings.
* Additionally, dark hours start time is supported to make use of minimum and maximum intensities.
* And to fulfill your all kinds of automation needs, app is supported as a Tasker plugin. You can use it with Profiles.
* Set on boot delay.
* Original KCAL settings of user is backed up and applied when night light is turned off. And it can be configured as well.
* Quick Setting tile for easy toggling on/off night light anywhere.
* Launcher icon shortcut for toggling Night Light on/off and toggling intensities.
* Dark and Light theme.

### Advantages
* __No overlays__ - As it uses KCAL to adjust screen colors.
* __No background service__ -  Once again, as it uses KCAL to adjust KCAL screen colors, it does not need to keep a background service running. It simply makes changes by updating KCAL values, which, once changed, will persist until a reboot is made (unless the values are changed). For automation, alarms are used.
* __Battery friendly alarm implementation__ - Alarms implemented (for automation) does not necessarily trigger at the exact specified time as they are needed to. They are only triggered at a given time when the screen is on at that time. Otherwise, they are triggered as soon as the user turns on the screen after the given time.

### Requirements
* Kernel supporting KCAL.
* Root access.

### Permissions
* Location - Needed to determine sunset/sunrise time for your current location.
* Internet - Internet connection is needed to determine coarse location when fine location is not available.
