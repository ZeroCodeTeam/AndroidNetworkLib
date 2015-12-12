package com.zerocodeteam.network.request;

import com.zerocodeteam.network.response.ResponseListener;

import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class StringRequest extends NetworkRequest<String> {

    /**
     * @param cookie       Metadata that are store locally and send back to requester
     * @param method       Type of HTTP method
     * @param url          URL of the request to make
     * @param headers      Map of request mHeaders
     * @param stringEntity Body content
     * @param listener     Handler for network response
     */
    public StringRequest(int method, String url, ResponseListener listener, Map<String, String> headers, StringEntity stringEntity, Object cookie) {
        super(method, url, listener, String.class, headers, stringEntity, cookie);
    }
}
