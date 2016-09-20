package com.zerocodeteam.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
abstract class NetworkRequest<T> extends Request<T> {

    private Class<T> mClass;
    private ZctResponse<T> mListener;
    private String mBodyContent;
    private String mBodyContentType;
    private Object mCookie;
    private Map<String, String> mRequestHeaders;
    private Map<String, String> mResponseHeaders;

    public NetworkRequest(ZctRequest.Builder requestObject) {
        super(requestObject.method, requestObject.url, null);
        this.mClass = requestObject.clazz;
        this.mListener = requestObject.callback;
        this.mBodyContent = ZctNetwork.getGsonInstance().toJson(requestObject.bodyContent);
        this.mBodyContentType = requestObject.bodyContentType;
        this.mCookie = requestObject.cookie;
        this.mRequestHeaders = requestObject.headers;
    }

    @Override
    public String toString() {
        String requestHeader = "not set";
        try {
            requestHeader = (mRequestHeaders != null ? mRequestHeaders : super.getHeaders()).toString();
        } catch (AuthFailureError afe) {
            ZctNetwork.log(afe.toString());
        }
        String ret = mClass + "\nHTTP method: " + getMethodName(this.getMethod()) + "\nUrl: " + this.getUrl() + "\nBody content: " + this.mBodyContent + "\nCookie: " + this.mCookie + "\nHeaders: " + requestHeader;
        return ret;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onSuccess(response, mResponseHeaders, mCookie);
        } else {
            ZctNetwork.log("Response listener is null");
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (error instanceof TimeoutError) {
            mListener.onError(error, ZctNetwork.ErrorType.TIMEOUT, mResponseHeaders, mCookie);
        } else if (error instanceof AuthFailureError) {
            mListener.onError(error, ZctNetwork.ErrorType.AUTH_FAILURE, mResponseHeaders, mCookie);
        } else if (error instanceof ServerError) {
            mListener.onError(error, ZctNetwork.ErrorType.SERVER_ERROR, mResponseHeaders, mCookie);
        } else if (error instanceof NetworkError) {
            mListener.onError(error, ZctNetwork.ErrorType.NETWORK_ERROR, mResponseHeaders, mCookie);
        } else if (error instanceof ParseError) {
            mListener.onError(error, ZctNetwork.ErrorType.PARSE_ERROR, mResponseHeaders, mCookie);
        } else {
            mListener.onError(error, ZctNetwork.ErrorType.UNKNOWN_ERROR, mResponseHeaders, mCookie);
        }
        ZctNetwork.log("Deliver error: " + error.getMessage());
    }

    @Override
    public String getBodyContentType() {
        ZctNetwork.log("Body type: " + (mBodyContentType != null ? mBodyContentType : super.getBodyContentType()));
        return mBodyContentType != null ? mBodyContentType : super.getBodyContentType();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mRequestHeaders != null ? mRequestHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mBodyContent == null || mBodyContent.equals("null")) {
            return super.getBody();
        }
        ZctNetwork.log("Body content: " + mBodyContent);
        return mBodyContent.getBytes();
    }

    /**
     * Watch out for NPE because GSON does not check schema.
     *
     * @param response
     * @return
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            mResponseHeaders = response.headers;
            String json;
            try {
                mResponseHeaders = response.headers;
                json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException uee) {
                ZctNetwork.log("Error charset parsing: " + uee);
                json = new String(response.data);
            }
            ZctNetwork.log("Response: " + json);
            if (mClass == String.class) {
                return Response.success((T) json, HttpHeaderParser.parseCacheHeaders(response));
            } else if (json.equals("")) {
                return Response.success(
                        (T) "Empty response",
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(
                        ZctNetwork.getGsonInstance().fromJson(json, mClass),
                        HttpHeaderParser.parseCacheHeaders(response));
            }

        } catch (UnsupportedOperationException uoe) {
            ZctNetwork.log("UnsupportedOperationException: " + uoe);
            return Response.error(new ParseError(response));
        } catch (JsonSyntaxException jse) {
            ZctNetwork.log("JsonSyntaxException: " + jse);
            return Response.error(new ParseError(response));
        }
    }

    private String getMethodName(Integer method) {
        String ret = "unknown";

        switch (method) {
            case 0:
                ret = "GET";
                break;
            case 1:
                ret = "POST";
                break;
            case 2:
                ret = "PUT";
                break;
            case 3:
                ret = "DELETE";
                break;
            case 4:
                ret = "HEAD";
                break;
            case 5:
                ret = "OPTIONS";
                break;
            case 6:
                ret = "TRACE";
                break;
            case 7:
                ret = "PATCH";
                break;
        }
        return ret;
    }
}
