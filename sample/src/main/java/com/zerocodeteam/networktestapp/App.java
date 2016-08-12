package com.zerocodeteam.networktestapp;

import android.app.Application;

import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class App extends Application {

    private static final Integer DEFAULT_DIALOG_TIME = 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        // Init network module
        // TODO Rework this instance
        new ZctNetwork.Builder(this).defaultLoggingEnabled(BuildConfig.DEBUG).defaultMinDialogTime(DEFAULT_DIALOG_TIME).build();
    }
}
