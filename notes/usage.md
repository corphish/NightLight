# F.A.Q
__Q. Why does the app require root access?__
A. Root access is required to modify KCAL values. This is impossible without root access.

__Q. What does the master switch do?__
A. It basically enables all the functions of the app. If master switch is off, none of the functions will be enabled, and vice versa.

__Q. OK then why does master switch exist? We would have to enable master switch anyway to use the app.__
A. Since Night Light supports features like automation and set on boot, it is necessary for such switch to exist, so that such external requests can be dealt with accordingly. Imagine you have this app installed, but for some reason you don\'t use the app, if master switch was not present at all, a previously set automation schedule could still turn on Night Light.

__Q. What does the KCAL backup options do?__
A. This option takes account of the user's custom KCAL settings. So that while turning off Night Light, the app is able to restore user's settings.

__Q. Since KCAL values are reset after boot, will the app will still be able function after that?__
A. Yes. After a reboot, app will re-apply night light values to KCAL, and re-set all the automation schedules. However it does not apply user\'s custom KCAL values (if night light should be off after boot). If for some reason, things were not applied properly after boot, consider increasing Set On boot delay. If the problem persists, please open an issue in github, or mention about the same in XDA thread. Links for them can be found in About section of the app.

__Q. What\'s with the various filtering modes?__
A. There are 2 filtering modes available. Color temperature provides a simple yet effective control for filtering out blue light. Range of color temperature supported is from 2500K to 5000K. And the other mode supported is manual KCAL color control to cater for custom RGB color value needs.

__Q. What are minimum and maximum intensities?__
A. Minimum and maximum intensities are 2 intensity types which lets the user to set different filter intensity values under each type and use them as per their choice. As the names suggest, it is advisable to set a lower intensity setting under minimum intensity, and a higher intensity under maximum intensity. Minimum intensity may then be used at evenings (dark but lighted environments) and maximum intensity may be used at nights (under pitch dark conditions).

__Q. Why does the app require Location permission?__
A. Location permission is required to determine sunset and sunrise timing for your location. Location permission is only asked when you enable automation and use sunset/sunrise timings. Your location is not shared or misused in anyway.

__Q. Is there any way for the user to toggle Night Light on/off without opening the app?__
A. Yes. Night Light supports toggling itself on/off from launcher shortcuts and through a tile in quick toggles. You need to add the tile in the quick toggles manually though.

__Q. How does the quick toggles work?__
A. Quick toggle can turn on/off Night Light irrespective of automation schedule. If however, the master switch is disabled, toggling on will turn on the master switch as well, so that Night Light can be turned on. Toggling off does not turn off the master switch though. It may seem complicated, but the way it works should provide a good user experience.

__Q. What are dark hours?__
A. Dark hours is a time period, usually from the time set by the user to the end of the automatic schedule, during which maximum intensity setting is applied. It works only if automation is enabled. Dark hours should usually be the time you turn off all your lights and go to bed (but still choose to use the device before falling asleep).

__Q. What is the point of having dark hours?__
A. The main purpose of dark hours feature is to give the intensity types (minimum and maximum intensity) an automatic control. If dark hours setting is set, minimum intensity is applied from start of the automatic period to this time, and the maximum intensity is applied until the end of the schedule.

