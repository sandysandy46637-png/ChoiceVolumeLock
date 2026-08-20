# Choice Volume Lock

LSPosed modern API 102 module for the Sony Xperia 1 III / Android 13 setup tested in this project.

## Target

`in.swiggy.deliveryapp.choice`

## What it does

Choice Delivery was observed calling:

`AudioManager.setStreamVolume(STREAM_MUSIC, 22, 0)`

The module intercepts `setStreamVolume()` inside the Choice process only. If the stream is `STREAM_MUSIC`, the call is blocked. It does not continuously reset the volume.

Therefore the volume remains whatever the user selected with the normal Android volume controls.

Examples:

- User volume 5 -> Choice request 22 -> remains 5
- User volume 15 -> Choice request 22 -> remains 15
- User volume 25 -> Choice request 22 -> remains 25
- Physical volume buttons -> not hooked
- Other apps -> not hooked

## Important first-test behavior

This is deliberately a narrow first build. It blocks every `STREAM_MUSIC` volume-setting call made by Choice, including a hypothetical legitimate Choice call that sets the volume lower. If testing shows Choice needs a more selective rule, the hook can be refined.

## Build

Open this project in Android Studio with a JDK suitable for Android Gradle Plugin 8.7.x.

Run:

`./gradlew :app:assembleDebug`

APK:

`app/build/outputs/apk/debug/app-debug.apk`

## Install

Install the APK normally. Do NOT install it as a Magisk module.

Then:

1. Open LSPosed Manager.
2. Enable `Choice Volume Lock`.
3. Scope it only to `in.swiggy.deliveryapp.choice`.
4. Force-stop Choice Delivery.
5. Launch Choice again.
6. Test with a low volume first.

## Logs

After testing:

`adb logcat -s ChoiceVolumeLock`

You should see:

`Hook installed for Choice`

and, when Choice tries to change the volume:

`Blocked Choice setStreamVolume(STREAM_MUSIC, 22, ...)`

## Safety

Keep the module scoped only to Choice. If Choice crashes or behaves unexpectedly, disable the module in LSPosed and force-stop/restart Choice.
