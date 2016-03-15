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
import com.google.gson.JsonSyntaxException;
import com.zerocodeteam.network.ZctNetwork;
import com.zerocodeteam.network.response.ResponseListener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public abstract class NetworkRequest<T> extends Request<T> {

    private static final String DEFAULT_BODY_CONTENT_TYPE = "application/json";

    private ResponseListener mListener;
    private Class<T> mClass;
    private Map<String, String> mRequestHeaders;
    private String mBodyContent;
    private String mBodyContentType;
    private Object mCookie;
    private Map<String, String> mResponseHeaders;

    public NetworkRequest(int method, String url, ResponseListener listener, Class<T> clazz, Object bodyContent, Object cookie) {
        super(method, url, null);
        this.mListener = listener;
        this.mClass = clazz;
        this.mBodyContent = ZctNetwork.getInstance().getGson().toJson(bodyContent);
        this.mCookie = cookie;
        this.mRequestHeaders = getDefaultRequestHeaders();
        this.mBodyContentType = getDefaultBodyContentType();
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponseSuccess(response, mResponseHeaders, mCookie);
            ZctNetwork.getInstance().dismissProgressDialog();
        }
        ZctNetwork.getInstance().dismissProgressDialog();
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (error instanceof TimeoutError) {
            mListener.onErrorResponse(error, ZctNetwork.ErrorType.TIMEOUT, mResponseHeaders, mCookie);
        } else if (error instanceof AuthFailureError) {
            mListener.onErrorResponse(error, ZctNetwork.ErrorType.AUTH_FAILURE, mResponseHeaders, mCookie);
        } else if (error instanceof ServerError) {
            mListener.onErrorResponse(error, ZctNetwork.ErrorType.SERVER_ERROR, mResponseHeaders, mCookie);
        } else if (error instanceof NetworkError) {
            mListener.onErrorResponse(error, ZctNetwork.ErrorType.NETWORK_ERROR, mResponseHeaders, mCookie);
        } else if (error instanceof ParseError) {
            mListener.onErrorResponse(error, ZctNetwork.ErrorType.PARSE_ERROR, mResponseHeaders, mCookie);
        } else {
            mListener.onErrorResponse(error, ZctNetwork.ErrorType.UNKNOWN_ERROR, mResponseHeaders, mCookie);
        }
        ZctNetwork.getInstance().dismissProgressDialog();
    }

    @Override
    public String getBodyContentType() {
        return mBodyContentType != null ? mBodyContentType : super.getBodyContentType();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mRequestHeaders != null ? mRequestHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mBodyContent == null) {
            return super.getBody();
        }
        return mBodyContent.getBytes();
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
                        ZctNetwork.getInstance().getGson().fromJson(json, mClass),
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

    public Map<String, String> getDefaultRequestHeaders() {
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Content-Type", "application/json");
        requestHeaders.put("Accept", "application/json");
        return requestHeaders;
    }

    public void setDefaultRequestHeaders(Map<String, String> requestHeaders) {
        this.mRequestHeaders = requestHeaders;
    }

    public String getDefaultBodyContentType() {
        return DEFAULT_BODY_CONTENT_TYPE;
    }

    public void setDefaultBodyContentType(String bodyContentType) {
        this.mBodyContentType = bodyContentType;
    }
}
