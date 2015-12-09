package com.zerocodeteam.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Bane on 23.7.2015.
 */
public class GsonRequest<T> extends NetworkRequest<T> {

    private final Gson mGson = new Gson();
    private final Class<T> mClass;

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param method   Method.GET or Method.POST ...
     * @param url      URL of the request to make
     * @param clazz    Relevant class object, for Gson's reflection
     * @param headers  Map of request mHeaders
     * @param listener
     */
    public GsonRequest(Object cookie, int method, String url, Class<T> clazz, Map<String, String> headers, StringEntity stringEntity,
                       ResponseListener<T> listener, Response.ErrorListener errorListener, Object...metaData) {
        super(cookie, method, url, headers, stringEntity, listener, errorListener, metaData);
        this.mClass = clazz;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            mResponseHeaders = response.headers;
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if (json.equals("")) {
                return Response.success(
                        null,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(
                        mGson.fromJson(json, mClass),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(response));
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return Response.error(new ParseError(response));
        }
    }
}
