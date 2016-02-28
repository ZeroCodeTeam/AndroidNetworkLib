package com.zerocodeteam.network.request;

import com.zerocodeteam.network.response.ResponseListener;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class GsonRequest<T> extends NetworkRequest<T> {

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param method       Type of HTTP method
     * @param url          URL of the request to make
     * @param listener     Handler for network response
     * @param clazz        Relevant class object, for Gson's reflection
     * @param headers      Map of request mHeaders
     * @param stringEntity Body content
     * @param cookie       Metadata that are store locally and send back to requester
     */
    public GsonRequest(int method, String url, ResponseListener<T> listener, Class<T> clazz, Map<String, String> headers, Object stringEntity, Object cookie) {
        super(method, url, listener, clazz, headers, stringEntity, cookie);
    }

}
