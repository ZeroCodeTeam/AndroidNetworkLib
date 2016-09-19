package com.zerocodeteam.networktestapp;

import android.content.Context;
import android.util.Log;

import com.zerocodeteam.network.ResponseListener;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.ZctRequest;


/**
 * Created by ZeroCodeTeam on 8/21/2015.
 */
public class API {

    //API urls
    private static final String API_BASE_URL = "http://echo.jsontest.com";
    private static final String API_PING_SERVER = "/key/value/one/two";
    private static String LOG = API.class.getSimpleName();

    /**
     * Test network call
     *
     * @param listener - Response listener
     */
    public static void echoCallString(ResponseListener listener, Context context) {

        // Perform request
        ZctRequest request = new ZctRequest.Builder(API_BASE_URL + API_PING_SERVER, listener).cookie("Dummy cookie").build();
        try {
            ZctNetwork.with(context).sendRequest(request);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }
    }

    /**
     * Test network call
     *
     * @param listener - Response listener
     */
    public static void echoCallGson(ResponseListener<Example> listener, Context context) {

        // Perform request
        ZctRequest request = new ZctRequest.Builder(API_BASE_URL + API_PING_SERVER, Example.class, listener).cookie("Dummy cookie").build();
        try {
            ZctNetwork.with(context).sendRequest(request);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }
    }

}
