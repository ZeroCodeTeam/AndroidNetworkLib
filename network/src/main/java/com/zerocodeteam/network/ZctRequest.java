package com.zerocodeteam.network;

import android.text.TextUtils;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctRequest extends NetworkRequest {

    private ZctRequest(Url requestObject) {
        super(requestObject);
    }

    /**
     * Fluent API for creating {@link StringRequest} instances.
     */
    public static class Url extends BaseUrl {

        public Url(String url) {
            super(url, String.class);
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Url must not be empty.");
            }
            super.url = url;
        }

        /**
         * Start building a new {@link StringRequest} instance.
         */
        public Url(String url, Class clazz) {
            super(url, clazz);
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Url must not be empty.");
            }
            super.url = url;
        }

        public Url method(Integer method) {
            super.method = method;
            return this;
        }

        public Url responseListener(ResponseListener responseListener) {
            super.responseListener = responseListener;
            return this;
        }

        public Url bodyContent(Object bodyContent) {
            super.bodyContent = bodyContent;
            return this;
        }

        public Url bodyContentType(String bodyContentType) {
            super.bodyContentType = bodyContentType;
            return this;
        }

        public Url cookie(Object cookie) {
            super.cookie = cookie;
            return this;
        }

        public Url headers(Map<String, String> requestHeaders) {
            super.headers = requestHeaders;
            return this;
        }

        /**
         * Create the {@link ZctNetwork} instance.
         */
        public ZctRequest build() {
            return new ZctRequest(this);
        }

    }
}
