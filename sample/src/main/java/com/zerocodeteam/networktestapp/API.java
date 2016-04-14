package com.zerocodeteam.networktestapp;

import android.content.Context;
import android.util.Log;

import com.zerocodeteam.network.ResponseListener;
import com.zerocodeteam.network.StringRequest;


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
    public static void echoCall(ResponseListener<String> listener, Context context) {

        // Perform request
//        GsonRequest<Example> request = new GsonRequest.Builder(API_BASE_URL + API_PING_SERVER).responseClazz(Example.class).responseListener(listener).method(Request.Method.GET).build();
        StringRequest request = new StringRequest.Builder(API_BASE_URL + API_PING_SERVER).responseListener(listener).build();
        try {
            NetworkTestApp.getNetworkInstance().with(context).executeRequest(request, false);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }

    }


}
