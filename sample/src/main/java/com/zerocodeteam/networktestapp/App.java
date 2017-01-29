package com.zerocodeteam.networktestapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by ZeroCodeTeam on 05/12/15.
 */
public class App extends Application {

    private static final String LOG_TAG = App.class.getSimpleName();

    private static ZctNetwork sNetwork;
    private static Context mContext;

    public static ZctNetwork getNetwork() {
        if (sNetwork == null) {
            sNetwork = new ZctNetwork.Builder(mContext).enableConsoleDebugging().build();
        }
        return sNetwork;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        // Configure default ZctNetwork settings
        sNetwork = new ZctNetwork.Builder(this).enableConsoleDebugging().build();

        Log.e(LOG_TAG, "Network connection type: " + sNetwork.isDeviceOnline());
        Log.e(LOG_TAG, sNetwork.readNetworkStats(ZctNetwork.NetworkStats.MOBILE_RX) + "");
        Log.e(LOG_TAG, sNetwork.readNetworkStats(ZctNetwork.NetworkStats.MOBILE_TX) + "");
        Log.e(LOG_TAG, sNetwork.readNetworkStats(ZctNetwork.NetworkStats.WIFI_RX) + "");
        Log.e(LOG_TAG, sNetwork.readNetworkStats(ZctNetwork.NetworkStats.WIFI_TX) + "");
    }
}
