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
        log(Log.INFO, TAG, "Module loaded");
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
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(chain -> {
                    Object streamArg = chain.getArg(0);
                    Object indexArg = chain.getArg(1);

                    int stream = ((Integer) streamArg).intValue();
                    int index = ((Integer) indexArg).intValue();

                    if (stream == AudioManager.STREAM_MUSIC) {
                        log(
                            Log.INFO,
                            TAG,
                            "Blocked Choice setStreamVolume(STREAM_MUSIC, " +
                            index + ", ...)"
                        );

                        // Do not call chain.proceed(): Choice's volume request
                        // is ignored. The existing user-selected volume remains.
                        return null;
                    }

                    return chain.proceed();
                });

            log(Log.INFO, TAG, "Hook installed for Choice");
        } catch (Throwable t) {
            log(Log.ERROR, TAG, "Failed to install hook: " + t);
        }
    }
}
