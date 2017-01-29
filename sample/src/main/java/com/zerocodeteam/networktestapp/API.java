package com.zerocodeteam.networktestapp;

import android.content.Context;
import android.util.Log;

import com.zerocodeteam.network.ZctRequest;
import com.zerocodeteam.network.ZctResponse;


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
    public static void echoCallString(ZctResponse listener, Context context) {

        // Perform request
        ZctRequest request = new ZctRequest.Builder(API_BASE_URL + API_PING_SERVER).response(listener).cookie("Dummy cookie").build();
        try {
            App.getNetwork().sendRequest(request);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }
    }

    /**
     * Test network call
     *
     * @param listener - Response listener
     */
    public static void echoCallGson(ZctResponse<Example> listener, Context context) {

        // Perform request
        ZctRequest request = new ZctRequest.Builder(API_BASE_URL + API_PING_SERVER).responseClass(Example.class).response(listener).cookie("Dummy cookie").build();
        try {
            App.getNetwork().sendRequest(request);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }
    }

}
