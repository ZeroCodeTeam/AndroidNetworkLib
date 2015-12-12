package com.zerocodeteam.networktestapp;

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

    /**
     * Starts network request
     *
     * @param request
     */
    private static void startNetworkRequest(Request request, String... tag) {
//        mLogger.d("STARTING REQUEST " + request.getUrl());
        if (tag.length == 0) {
            ZctNetwork.getInstance().sendRequest(request);
        } else {
            ZctNetwork.getInstance().sendRequest(request, tag[0]);
        }
    }

    /**
     * Performs login procedure for given loginData
     *
     * @param listener
     */
    public static void pingServer(ResponseListener<String> listener) {

        // Perform request
        StringRequest request = new StringRequest(Request.Method.POST, API_BASE_URL + API_PING_SERVER, listener, generateDefaultHeaders(), null, null);
        startNetworkRequest(request);
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
