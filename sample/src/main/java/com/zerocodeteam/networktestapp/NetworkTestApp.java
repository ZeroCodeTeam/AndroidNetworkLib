package com.zerocodeteam.networktestapp;

import android.app.Application;

import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class NetworkTestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init network module
        ZctNetwork.getInstance().init(this, 1000);
    }
}
