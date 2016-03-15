package com.zerocodeteam.network.request;

import com.zerocodeteam.network.response.ResponseListener;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class StringRequest extends NetworkRequest<String> {

    /**
     * @param cookie       Metadata that are store locally and send back to requester
     * @param method       Type of HTTP method
     * @param url          URL of the request to make
     * @param stringEntity Body content
     * @param listener     Handler for network response
     */
    public StringRequest(int method, String url, ResponseListener listener, Object stringEntity, Object cookie) {
        super(method, url, listener, String.class, stringEntity, cookie);
    }
}
