package com.zerocodeteam.networktestapp;

import android.app.Application;
import android.util.Log;

import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class App extends Application {

    private static final Integer DEFAULT_DIALOG_TIME = 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        // Reconfigure default ZctNetwork settings
        ZctNetwork zct = new ZctNetwork.Builder(this).defaultLoggingEnabled(BuildConfig.DEBUG).defaultMinDialogTime(DEFAULT_DIALOG_TIME).build();

        Log.e(App.class.getSimpleName(), "Network connection type: " + ZctNetwork.isDeviceOnline(getApplicationContext()));
    }
}
