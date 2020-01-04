# Automation 2.0
The main objective of this readme is to discuss the automation model present on versions 2.2.x and below, its limitations and lay out the plans for a new automation model.

### Existing automation
The existing model (the one present in v2.2.x and below) provides a `start time` parameter and an `end time` parameter, in which, Night Light will be turned on at start time and will be turned off at end time. Additionally, a `dark start time` parameter was introduced later on along with `minimum` and `maximum` intensity parameters, in which the `minimum` intensities would be applied during `start time` and `maximum` intensities would be applied during `dark start time`.

All the triggers are done using Android's `AlarmManager` API, avoiding unnecessary wakeups for Night Light applications, which means if your device is in deep sleep during a trigger time, the state won't be interrupted and your device will continue to be in deep sleep, and the pending trigger will happen as soon as you turn on the screen of your device. This is extremely important (battery-wise) and practical because there is no point in breaking deep sleep and apply KCAL on a screen that is turned off. In this way, not only battery is preserved, but KCAL is applied only when its necessary. As it has been always been emphasized, **Night Light always strives for efficiency.** And with this, battery efficiency is achieved.

#### Features
* Easy to use.
* Achieves both battery and memory efficiency (because a background service is not used).
* IMO, current approach is more practical than gradual Night Light fading.


#### Y no u add Night Light fading?
Implementation of Night Light fading (from what I understand, Night Light will gradually be intensified or lessened with time) has been **the most requested feature**, something that I am very well aware of, yet I have never decided to work on it. The reasons for the same are:

* It will require a background service to be run during the active period, which will constantly change KCAL values (even when the screen is off, which as explained as above, completely unnecessary). This is quite inefficient because every time when it has to apply, it needs to open a root session and update KCAL, which could take a great toll on the battery life of the device. Maybe in implementations of other similar apps, it is not as inefficient but considering the way this app works, it definitely is inefficient.
* Having a background service violates our principle of achieving memory efficiency.
* Lastly, I personally find gradual Night Light fading not much practical. For example, considering sunset/sunrise times as it is a widely used schedule, Night Light is turned on  during start time, which usually is at 6.30pm during summers and 5.30pm during winters at my place. So usually during start time, lights of our rooms are turned on, and it stays on until bedtime (which is usually 12:00am for me). This is the usual habit for most of us I believe. So if during start time the temperature applied is, let's say, 4000K, is it really practical to have an intensified 3500K temperature at 11.59pm (when lights are still on)? Won't it make sense to have the same 4000K at 11.59pm and the 3500K at 12.00am (when lights are off). Which is why `dark start time` was introduced, because many people tend to use their phones during pitch dark conditions (basically bed time). And to my knowledge, not even OEMs implement this feature.

But then again, everyone has their own use cases, and I am sure that gradual Night Light fading is a part of use cases of many, which is why it has been requested so many times. But considering the cons it brings to the table, I thought it is better  to not implement this.