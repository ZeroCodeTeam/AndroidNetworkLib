package com.zerocodeteam.network.request;

import com.zerocodeteam.network.response.ResponseListener;

import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class GsonRequest<T> extends NetworkRequest<T> {

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param cookie       Metadata that are store locally and send back to requester
     * @param method       Type of HTTP method
     * @param url          URL of the request to make
     * @param clazz        Relevant class object, for Gson's reflection
     * @param headers      Map of request mHeaders
     * @param stringEntity Body content
     * @param listener     Handler for network response
     */
    public GsonRequest(Object cookie, int method, String url, Class<T> clazz, Map<String, String> headers, StringEntity stringEntity,
                       ResponseListener<T> listener) {
        super(cookie, method, url, clazz, headers, stringEntity, listener);
    }

}
