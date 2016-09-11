package com.zerocodeteam.networktestapp;

import android.content.Context;
import android.util.Log;

import com.zerocodeteam.network.GsonRequest;
import com.zerocodeteam.network.ResponseListener;
import com.zerocodeteam.network.StringRequest;
import com.zerocodeteam.network.ZctNetwork;


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
        StringRequest request = new StringRequest.Builder(API_BASE_URL + API_PING_SERVER).cookie("Dummy cookie").responseListener(listener).build();
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
//        StringRequest request = new StringRequest.Builder(API_BASE_URL + API_PING_SERVER).cookie("Dummy cookie").responseListener(listener).build();
        GsonRequest request = new GsonRequest.Builder(API_BASE_URL + API_PING_SERVER).responseClazz(Example.class).cookie("Dummy cookie").responseListener(listener).build();
        try {
            ZctNetwork.with(context).sendRequest(request);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }
    }

}
