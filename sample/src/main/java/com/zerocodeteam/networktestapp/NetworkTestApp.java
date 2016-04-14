package com.zerocodeteam.networktestapp;

import android.app.Application;
import android.util.Log;

import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class NetworkTestApp extends Application {

    private static ZctNetwork sInstance;

    public static ZctNetwork getNetworkInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init network module
        sInstance = new ZctNetwork.Builder(this).defaultDialogMsg("Ucitavanje. Molimo sacekajte").defaultLoggingEnabled(true).build();
        Log.e("APP", "ON CREATE");
    }
}
