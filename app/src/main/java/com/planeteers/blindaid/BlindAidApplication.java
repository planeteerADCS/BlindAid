package com.planeteers.blindaid;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Jose on 11/9/15.
 */
public class BlindAidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }
}
