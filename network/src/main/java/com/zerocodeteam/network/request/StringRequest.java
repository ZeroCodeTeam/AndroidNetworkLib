package com.zerocodeteam.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class StringRequest extends NetworkRequest<String> {

    public StringRequest(Object cookie, int method, String url, Map<String, String> headers, StringEntity stringEntity, ResponseListener successListener) {
        super(cookie, method, url, headers, stringEntity, successListener);
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            mResponseHeaders = response.headers;
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException var4) {
            parsed = new String(response.data);
        }

        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
