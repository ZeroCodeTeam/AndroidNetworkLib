package com.zerocodeteam.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

/**
 * Created by rade on 14/04/16.
 */
public class ZctNetworkUtils {

    private Gson sGson;

    public ZctNetworkUtils() {
        sGson = new Gson();
    }

    /**
     * Checks if there is network connectivity
     *
     * @return TRUE - connected, FALSE - not
     */
    public boolean isDeviceOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public Gson getGson() {
        return this.sGson;
    }
}
