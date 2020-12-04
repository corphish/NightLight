# Automation Routines
Automation routines lets you define the Night Light settings that needs to be applied in a certain time period. It gives you full control in defining a schedule and what settings should be applied in that schedule, and it even lets you define the fade behaviors for that time period.

### Creating a routine
To create a routine, head over to the automation section first.
- Open the app.
- Tap the settings button in the bottom part of the screen.
- Go to `Automation` section.
- Go to `Set up automation` section.

Here you will see the routines that you have created. Initially, it would be empty as there is no routine created. To create a routine, tap `+` button in the bottom part of the screen. This will take you to a screen where you can create the routine. It is really easy to create a routine, as you will need to provide data in a form presented to you as following steps.
- __Night Light Switch__ - Whether you want to have the night light on or off as part of the routine.
- __Start time__ - Start time of the routine. You can provide a custom time, or set the time to be sunset or sunrise, which will be resolved everyday. You may be warned if you try to set a time which overlaps with other routines, in such cases please supply a different time as overlapping routines are not supported.
- __End time__ - End time of the routine. You can provide a custom time, or set the time to be sunset or sunrise, which will be resolved everyday. This step is optional if you chose to disable night light in the routine. You may be warned if you try to set a time which overlaps with other routines, in such cases please supply a different time as overlapping routines are not supported.
- __Fade behavior__ - You can supply the fading behavior for the Night Light values here. The behavior will only effect within the time period of the routine. This step is optional if you chose to disable night light in the routine. There are 3 types of behaviors.
	- __Fade Off__ - There will be no fading, and only the selected value will be applied throughout the time period of the routine.
	- __Fade in__ - The night light intensity will fade in from a higher RGB value to a lower RGB value. In the upcoming sections, you will be asked to enter a starting RGB value and an ending RGB value, and the Night Light will fade in from the starting value to ending value. It is expected that the starting RGB value should be higher than the ending one, but the app will adjust the RGB inputs accordingly to match the set behavior.
	- __Fade out__ - The night light intensity will fade out from a lower RGB value to a higher RGB value. In the upcoming sections, you will be asked to enter a starting RGB value and an ending RGB value, and the Night Light will fade out from the starting value to ending value. It is expected that the starting RGB value should be lower than the ending one, but the app will adjust the RGB inputs accordingly to match the set behavior.
- __Starting RGB value__ - Starting RGB value. This value is also applied through out if Fade off behavior is set. This step is optional if you chose to disable night light in the routine.
- __Ending RGB value__ - Ending RGB value. This step is optional if you chose to disable night light in the routine or you chose to not use any fading in the routine.
- __Name__ - Name of the routine, it does not have to be unique though, but a non-empty name must be supplied.

Once done, you can review the steps and confirm, which will bring you to the previous screen where you can see your newly created routine. You create another routine, or modify an existing one in this screen.