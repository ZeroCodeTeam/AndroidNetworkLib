package com.zerocodeteam.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.response.ResponseListener;

import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public abstract class NetworkRequest<T> extends Request<T> {

    private Object mCookie;
    private Class<T> mClass;
    private Map<String, String> mHeaders;
    private StringEntity mStringEntity;
    private ResponseListener mListener;
    private Map<String, String> mResponseHeaders;

    public NetworkRequest(Object cookie, int method, String url, Class<T> clazz, Map<String, String> headers, StringEntity stringEntity, ResponseListener listener) {
        super(method, url, null);
        this.mHeaders = headers;
        this.mStringEntity = stringEntity;
        this.mListener = listener;
        this.mCookie = cookie;
        this.mClass = clazz;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponseSuccess(mCookie, response, mResponseHeaders);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (error instanceof TimeoutError) {
            mListener.onErrorResponse(ZctNetwork.ErrorType.TIMEOUT, mCookie, error, mResponseHeaders);
        } else if (error instanceof AuthFailureError) {
            mListener.onErrorResponse(ZctNetwork.ErrorType.AUTH_FAILURE, mCookie, error, mResponseHeaders);
        } else if (error instanceof ServerError) {
            mListener.onErrorResponse(ZctNetwork.ErrorType.SERVER_ERROR, mCookie, error, mResponseHeaders);
        } else if (error instanceof NetworkError) {
            mListener.onErrorResponse(ZctNetwork.ErrorType.NETWORK_ERROR, mCookie, error, mResponseHeaders);
        } else if (error instanceof ParseError) {
            mListener.onErrorResponse(ZctNetwork.ErrorType.PARSE_ERROR, mCookie, error, mResponseHeaders);
        } else {
            mListener.onErrorResponse(ZctNetwork.ErrorType.UNKNOWN_ERROR, mCookie, error, mResponseHeaders);
        }

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mStringEntity != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                mStringEntity.writeTo(outputStream);
            } catch (IOException e) {
                VolleyLog.e("IOException @ " + getClass().getSimpleName());
            }
            return outputStream.toByteArray();
        } else {
            return super.getBody();
        }
    }

    @Override
    public String getBodyContentType() {
        return mStringEntity != null ? mStringEntity.getContentType().getValue() : super.getBodyContentType();
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            mResponseHeaders = response.headers;
            String json;
            try {
                mResponseHeaders = response.headers;
                json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.e("Error charset parsing: %s", uee);
                json = new String(response.data);
            }
            if (mClass == String.class) {
                return Response.success((T) json, HttpHeaderParser.parseCacheHeaders(response));
            } else if (json.equals("")) {
                return Response.success(
                        (T) "Empty response",
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(
                        new Gson().fromJson(json, mClass),
                        HttpHeaderParser.parseCacheHeaders(response));
            }

        } catch (UnsupportedOperationException uoe) {
            VolleyLog.e("UnsupportedOperationException: %s", uoe);
            return Response.error(new ParseError(response));
        } catch (JsonSyntaxException jse) {
            VolleyLog.e("JsonSyntaxException: %s", jse);
            return Response.error(new ParseError(response));
        }
    }
}
