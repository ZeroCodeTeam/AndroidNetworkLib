package com.zerocodeteam.networktestapp;

import com.android.volley.Request;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.request.GsonRequest;
import com.zerocodeteam.network.response.ResponseListener;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;


/**
 * Created by ZeroCodeTeam on 8/21/2015.
 */
public class API {


    private static final String API_REQUEST_TAG = "article";

    //API urls
    private static final String API_BASE_URL = "http://onlinews.dis.rs/webservices/";
    private static final String API_PING_SERVER = "server-status.php";

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
    public static void pingServer(ResponseListener<Object> listener) {

        // Perform request
        GsonRequest<Object> request = new GsonRequest<>(null, Request.Method.POST, API_BASE_URL + API_PING_SERVER, null, generateDefaultHeaders(), null, listener);
        startNetworkRequest(request);
    }


    /**
     * Converts model object to request body
     */
    private static StringEntity transformModelToEntity(Object object) {
        //Populate string entity
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(object.toString(), "UTF-8");
//            mLogger.d("transformModelToEntity: " + object.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return stringEntity;
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
