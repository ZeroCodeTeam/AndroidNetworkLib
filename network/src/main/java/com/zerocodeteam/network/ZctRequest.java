package com.zerocodeteam.network;

import android.text.TextUtils;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctRequest<T> extends Request<T> {

    private String mUrl;
    private Class<T> mResponseClass;
    private ZctRequest.Method mMethod;
    private ZctResponse<T> mResponse;
    private String mBodyContent;
    private String mBodyContentType;
    private Object mCookie;
    private Map<String, String> mRequestHeaders;
    private Map<String, String> mResponseHeaders;

    private ZctRequest(ZctRequest.Builder builder) {
        super(builder.method.getValue(), builder.url, null);

        this.mUrl = builder.url;
        this.mMethod = builder.method;
        this.mResponse = builder.response;
        this.mResponseClass = builder.responseClazz;
        this.mBodyContent = builder.bodyContent;
        this.mBodyContentType = builder.bodyContentType;
        this.mRequestHeaders = builder.headers;
        this.mCookie = builder.cookie;

        ZctNetwork.log("Request: \n" +
                builder);
    }

    private static String getMethodName(Integer method) {
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

    @Override
    protected void deliverResponse(T response) {
        ZctNetwork.log("Response:\n" +
                response.toString());

        if (mResponse != null) {
            mResponse.onSuccess(response, mResponseHeaders, mCookie);
        } else {
            ZctNetwork.log("Response listener is null");
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        ZctNetwork.log("Response finished with error!");
        if (mResponse == null ) {
            ZctNetwork.log("Response listener is null");
            return;
        }
        if (error instanceof TimeoutError) {
            mResponse.onError(error, ZctNetwork.ErrorType.TIMEOUT, mResponseHeaders, mCookie);
        } else if (error instanceof AuthFailureError) {
            mResponse.onError(error, ZctNetwork.ErrorType.AUTH_FAILURE, mResponseHeaders, mCookie);
        } else if (error instanceof ServerError) {
            mResponse.onError(error, ZctNetwork.ErrorType.SERVER_ERROR, mResponseHeaders, mCookie);
        } else if (error instanceof NetworkError) {
            mResponse.onError(error, ZctNetwork.ErrorType.NETWORK_ERROR, mResponseHeaders, mCookie);
        } else if (error instanceof ParseError) {
            mResponse.onError(error, ZctNetwork.ErrorType.PARSE_ERROR, mResponseHeaders, mCookie);
        } else {
            mResponse.onError(error, ZctNetwork.ErrorType.UNKNOWN_ERROR, mResponseHeaders, mCookie);
        }
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
        if (mBodyContent == null || mBodyContent.equals("null")) {
            return super.getBody();
        }
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
        ZctNetwork.log("[" + response.networkTimeMs + " ms] >> " + mUrl);

        mResponseHeaders = response.headers;
        String responseStr;

        try {
            responseStr = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            if (mResponseClass == String.class) {
                return Response.success((T) responseStr, HttpHeaderParser.parseCacheHeaders(response));
            } else if (responseStr.equals("")) {
                return Response.success(
                        (T) "Empty response",
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(
                        ZctNetwork.getGson().fromJson(responseStr, mResponseClass),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    public enum Method {
        GET(0),
        POST(1),
        PUT(2),
        DELETE(3),
        HEAD(4),
        OPTIONS(5),
        TRACE(6),
        PATCH(7);

        private final String name;
        private final Integer value;

        Method(Integer s) {
            name = getMethodName(s);
            value = s;
        }

        public boolean equals(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        Integer getValue() {
            return value;
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Fluent API for creating {@link ZctRequest} instances.
     */
    public static class Builder {
        private static String DEFAULT_BODY_CONTENT_TYPE = "application/json";
        private static Method DEFAULT_METHOD = Method.GET;

        private String url = null;
        private Method method = null;
        private ZctResponse response = null;
        private Class responseClazz = null;
        private String bodyContent = null;
        private String bodyContentType = null;
        private Map<String, String> headers = null;
        private Object cookie = null;

        /**
         * Start building a new {@link ZctRequest} instance.
         */
        public Builder(String url) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("URL must not be empty.");
            }
            this.url = url;
        }

        /**
         * Change default GET method to POST/PUT...
         *
         * @param method - Http method type.
         * @return - Instance of Builder object.
         */
        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        /**
         * Register response from sent request.
         *
         * @param response - Interface that will be called as soon as response is received.
         * @return - Instance of Builder object.
         */
        public Builder response(ZctResponse response) {
            if (response == null) {
                throw new IllegalArgumentException("Callback must not be null.");
            }
            this.response = response;
            return this;
        }

        /**
         * @param responseClazz - Instance of this class will be returned as response to sent request.
         * @return - Instance of Builder object.
         */
        public Builder responseClass(Class responseClazz) {
            if (responseClazz == null) {
                throw new IllegalArgumentException("Class must not be null.");
            }
            this.responseClazz = responseClazz;
            return this;
        }

        /**
         * @param bodyContent - Body content.
         * @return - Instance of Builder object.
         */
        public Builder bodyContent(String bodyContent) {
            this.bodyContent = bodyContent;
            return this;
        }

        /**
         * @param bodyContentType - Body content type.
         * @return - Instance of Builder object.
         */
        public Builder bodyContentType(String bodyContentType) {
            this.bodyContentType = bodyContentType;
            return this;
        }

        /**
         * @param requestHeaders - Request headers that will be send with request.
         * @return - Instance of Builder object.
         */
        public Builder headers(Map<String, String> requestHeaders) {
            this.headers = requestHeaders;
            return this;
        }

        /**
         * This object will be returned with response to any request.
         *
         * @param cookie - Metadata.
         * @return - Instance of Builder object.
         */
        public Builder cookie(Object cookie) {
            this.cookie = cookie;
            return this;
        }

        /**
         * Create the {@link ZctNetwork} instance.
         */
        public ZctRequest build() {

            if (this.responseClazz == null) {
                this.responseClazz = String.class;
            }

            if (this.method == null) {
                this.method = DEFAULT_METHOD;
            }

            if (this.bodyContentType == null) {
                bodyContentType = DEFAULT_BODY_CONTENT_TYPE;
            }

            if (this.headers == null) {
                headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
            }

            return new ZctRequest(this);
        }

        @Override
        public String toString() {
            return "method=" + method +
                    ",\nurl='" + url + '\'' +
                    ",\nresponseClazz=" + responseClazz +
                    ",\nresponse=" + response +
                    ",\nbodyContent='" + bodyContent + '\'' +
                    ",\nbodyContentType='" + bodyContentType + '\'' +
                    ",\ncookie=" + cookie +
                    ",\nheaders=" + headers;
        }
    }
}
