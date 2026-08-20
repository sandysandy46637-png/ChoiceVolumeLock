package com.prudhviraj.choicevolumelock;

import android.media.AudioManager;
import android.util.Log;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

public final class ChoiceVolumeModule extends XposedModule {

    private static final String TAG = "ChoiceVolumeLock";
    private static final String TARGET = "in.swiggy.deliveryapp.choice";

    @Override
    public void onModuleLoaded(ModuleLoadedParam param) {
        log(Log.INFO, TAG, "Choice Volume Lock loaded");
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        if (!TARGET.equals(param.getPackageName())) {
            return;
        }

        try {
            Method method = AudioManager.class.getDeclaredMethod(
                    "setStreamVolume",
                    int.class,
                    int.class,
                    int.class
            );

            hook(method)
                    .setPriority(XposedInterface.PRIORITY_HIGHEST)
                    .setExceptionMode(
                            XposedInterface.ExceptionMode.PROTECTIVE
                    )
                    .intercept(chain -> {

                        int stream = (Integer) chain.getArg(0);
                        int requested = (Integer) chain.getArg(1);

                        if (stream == AudioManager.STREAM_MUSIC) {
                            try {
                                AudioManager audioManager =
                                        (AudioManager) chain.getThisObject();

                                int current =
                                        audioManager.getStreamVolume(
                                                AudioManager.STREAM_MUSIC
                                        );

                                if (requested > current) {
                                    log(
                                            Log.INFO,
                                            TAG,
                                            "BLOCKED Choice volume increase: "
                                                    + current
                                                    + " -> "
                                                    + requested
                                    );

                                    return null;
                                }

                                log(
                                        Log.INFO,
                                        TAG,
                                        "Allowed Choice volume change: "
                                                + current
                                                + " -> "
                                                + requested
                                );

                            } catch (Throwable t) {
                                log(
                                        Log.ERROR,
                                        TAG,
                                        "Could not read current volume; "
                                                + "allowing original call",
                                        t
                                );
                            }
                        }

                        return chain.proceed();
                    });

            log(Log.INFO, TAG, "Hook installed for Choice");

        } catch (Throwable t) {
            log(
                    Log.ERROR,
                    TAG,
                    "Hook installation failed",
                    t
            );
        }
    }
}
