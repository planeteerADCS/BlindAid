package com.planeteers.blindaid;

import android.app.Application;

import com.parse.Parse;

import timber.log.Timber;

/**
 * Created by Jose on 11/9/15.
 */
public class BlindAidApplication extends Application {

    public final String PARSE_APP_ID = "3reP8WePd3FG2VwFnrxQLZL1zbsNPB3a0oMQMIJ1";
    public final String PARSE_CLIENT_KEY =  "oMxr6E8aUtB6YeGWC2vxIlaGfk7WarQ82N4066SH";

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);

    }
}
