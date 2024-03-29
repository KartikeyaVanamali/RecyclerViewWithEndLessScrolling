package com.sample.myapplication;

import android.app.Application;

import timber.log.Timber;

public class UberApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
