package com.prudhviraj.choicevolumelock;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

import java.lang.reflect.Method;

public final class ChoiceVolumeModule extends XposedModule {

    private static final String TAG = "ChoiceVolumeLock";
    private static final String TARGET = "in.swiggy.deliveryapp.choice";

    @Override
    public void onModuleLoaded(ModuleLoadedParam param) {
        log(Log.INFO, TAG, "Module loaded");
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        if (!TARGET.equals(param.getPackageName())) {
            return;
        }

        log(Log.INFO, TAG, "Choice package loaded");

        hookStreamVolume();
        hookAudioTrackVolume();
    }

    private void hookStreamVolume() {
        try {
            Method method = AudioManager.class.getDeclaredMethod(
                    "setStreamVolume",
                    int.class,
                    int.class,
                    int.class
            );

            hook(method)
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {

                        int stream = (Integer) chain.getArg(0);
                        int requested = (Integer) chain.getArg(1);

                        if (stream == AudioManager.STREAM_MUSIC) {
                            log(
                                    Log.INFO,
                                    TAG,
                                    "BLOCK Choice setStreamVolume(STREAM_MUSIC, "
                                            + requested + ", ...)"
                            );
                            return null;
                        }

                        return chain.proceed();
                    });

            log(
                    Log.INFO,
                    TAG,
                    "AudioManager.setStreamVolume hook installed"
            );

        } catch (Throwable t) {
            log(
                    Log.ERROR,
                    TAG,
                    "Failed installing AudioManager hook",
                    t
            );
        }
    }

    private void hookAudioTrackVolume() {
        try {
            Method method = AudioTrack.class.getDeclaredMethod(
                    "setVolume",
                    float.class,
                    float.class
            );

            hook(method)
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(chain -> {

                        float left = (Float) chain.getArg(0);
                        float right = (Float) chain.getArg(1);

                        log(
                                Log.INFO,
                                TAG,
                                "Choice AudioTrack.setVolume("
                                        + left + ", " + right + ")"
                        );

                        return chain.proceed();
                    });

            log(
                    Log.INFO,
                    TAG,
                    "AudioTrack.setVolume hook installed"
            );

        } catch (Throwable t) {
            log(
                    Log.ERROR,
                    TAG,
                    "Failed installing AudioTrack hook",
                    t
            );
        }
    }
}
