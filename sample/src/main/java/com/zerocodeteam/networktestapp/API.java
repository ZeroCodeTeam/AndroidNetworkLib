package com.zerocodeteam.networktestapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.request.StringRequest;
import com.zerocodeteam.network.response.ResponseListener;


/**
 * Created by ZeroCodeTeam on 8/21/2015.
 */
public class API {

    //API urls
//    private static final String API_BASE_URL = "http://echo.jsontest.com";
//    private static final String API_PING_SERVER = "/key/value/one/two";
    private static final String API_BASE_URL = "http://ws.velikisrpskikuvar.rs/webservices";
    private static final String API_PING_SERVER = "/prijava.php";
    private static String LOG = API.class.getSimpleName();


    /**
     * Test network call
     *
     * @param listener - Response listener
     */
    public static void echoCall(ResponseListener<String> listener, Context context) {

        String data = new String("{\n" +
                "  \"korisnik\": \"string\",\n" +
                "  \"lozinka\": \"string\"\n" +
                "}");
        // Perform request
        StringRequest request = new StringRequest(Request.Method.POST, API_BASE_URL + API_PING_SERVER, listener, data, null);
        try {
            ZctNetwork.getInstance().sendRequest(request, context);
        } catch (IllegalStateException ise) {
            Log.e(LOG, ise.toString());
        }

    }


}
