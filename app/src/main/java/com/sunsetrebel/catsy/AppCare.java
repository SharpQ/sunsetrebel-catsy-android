package com.sunsetrebel.catsy;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Track the current activity using the LifecycleCallback application
 */
public enum AppCare {
    YesSir;

    private Activity liveActivityOrNull;

    public void takeCareOn(Application application) {
        registerActivityLifeCycle(application);
        setDefaultUncaughtExceptionHandler();
    }

    private void registerActivityLifeCycle(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                liveActivityOrNull = activity;
            }

            @Override public void onActivityStarted(Activity activity) {}

            @Override public void onActivityResumed(Activity activity) {
                liveActivityOrNull = activity;
            }

            @Override public void onActivityPaused(Activity activity) {
                liveActivityOrNull = null;
            }

            @Override public void onActivityStopped(Activity activity) {}

            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override public void onActivityDestroyed(Activity activity) {}
        });
    }

    /**
     * In case you want to ensure exceptions are informed without user submission
     */
    private void setDefaultUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            defaultHandler.uncaughtException(thread, ex);
        });
    }

    @Nullable
    public Activity getLiveActivityOrNull() {
        return liveActivityOrNull;
    }
}
