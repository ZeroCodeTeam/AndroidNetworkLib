package com.zerocodeteam.networktestapp;

import android.util.Log;

import com.android.volley.Request;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.request.StringRequest;
import com.zerocodeteam.network.response.ResponseListener;

import java.util.HashMap;


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
    public static void echoCall(ResponseListener<String> listener) {

        // Perform request
        StringRequest request = new StringRequest(Request.Method.POST, API_BASE_URL + API_PING_SERVER, listener, generateDefaultHeaders(), null, null);
        try {
            ZctNetwork.getInstance().sendRequest(request);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }

    }

    /**
     * @return Generated default JSON headers
     */
    private static HashMap<String, String> generateDefaultHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }
}
