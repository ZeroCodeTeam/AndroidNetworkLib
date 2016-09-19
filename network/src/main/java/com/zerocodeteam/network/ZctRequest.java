package com.zerocodeteam.network;

import android.text.TextUtils;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctRequest extends NetworkRequest {

    private ZctRequest(Builder requestObject) {
        super(requestObject);
    }

    /**
     * Fluent API for creating {@link ZctRequest} instances.
     */
    public static class Builder {

        private static Integer DEFAULT_METHOD = Request.Method.GET;
        private static String DEFAULT_BODY_CONTENT_TYPE = "application/json";

        protected Integer method = -1;
        protected String url;
        protected Class clazz;
        protected ResponseListener responseListener;
        protected Object bodyContent;
        protected String bodyContentType;
        protected Object cookie;
        protected Map<String, String> headers;

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
         * Start building a new {@link ZctRequest} instance.
         */
        public Builder(String url, ResponseListener responseListener) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("URL must not be empty.");
            }
            this.url = url;

            if (responseListener == null) {
                throw new IllegalArgumentException("Response listener must not be null.");
            }
            this.responseListener = responseListener;
        }

        /**
         * Start building a new {@link ZctRequest} instance.
         */
        public Builder(String url, Class clazz, ResponseListener responseListener) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Builder must not be empty.");
            }
            this.url = url;

            if (clazz == null) {
                throw new IllegalArgumentException("Class must not be null.");
            }
            this.clazz = clazz;

            if (responseListener == null) {
                throw new IllegalArgumentException("Response listener must not be null.");
            }
            this.responseListener = responseListener;
        }

        public Builder method(Integer method) {
            this.method = method;
            return this;
        }

        public Builder responseListener(ResponseListener responseListener) {
            this.responseListener = responseListener;
            return this;
        }

        public Builder bodyContent(Object bodyContent) {
            this.bodyContent = bodyContent;
            return this;
        }

        public Builder bodyContentType(String bodyContentType) {
            this.bodyContentType = bodyContentType;
            return this;
        }

        public Builder cookie(Object cookie) {
            this.cookie = cookie;
            return this;
        }

        public Builder headers(Map<String, String> requestHeaders) {
            this.headers = requestHeaders;
            return this;
        }

        /**
         * Create the {@link ZctNetwork} instance.
         */
        public ZctRequest build() {
            if (this.method == -1) {
                this.method = DEFAULT_METHOD;
            }

            if (this.bodyContentType == null) {
                bodyContentType = DEFAULT_BODY_CONTENT_TYPE;
            }

            if (this.clazz == null) {
                this.clazz = String.class;
            }

            if (this.headers == null) {
                headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
            }

            return new ZctRequest(this);
        }

    }
}
