package com.zerocodeteam.networktestapp;

import android.app.Application;

import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class App extends Application {

    private static final String DEFAULT_MSG = "Loading. Please wait...";
    private static final Integer DEFAULT_DIALOG_TIME = 1000;

    private static ZctNetwork sInstance;

    public static ZctNetwork getNetworkInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init network module
        sInstance = new ZctNetwork.Builder(this).defaultDialogMsg(DEFAULT_MSG).defaultLoggingEnabled(BuildConfig.DEBUG).defaultMinDialogTime(DEFAULT_DIALOG_TIME).build();
    }
}
