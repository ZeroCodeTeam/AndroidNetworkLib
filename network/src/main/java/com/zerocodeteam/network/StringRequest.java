package com.zerocodeteam.network;

import android.text.TextUtils;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class StringRequest extends NetworkRequest<String> {

    private StringRequest(Builder builder) {
        super(builder.method, builder.url, builder.responseListener, String.class, builder.requestData, builder.cookie);
        ZctNetwork.log(StringRequest.class.getSimpleName() + " object created");
    }

    /**
     * Fluent API for creating {@link StringRequest} instances.
     */
    public static class Builder {
        private static Integer DEFAULT_METHOD = Method.GET;

        private String url;
        private Integer method;
        private ResponseListener responseListener;
        private Object requestData;
        private Object cookie;

        /**
         * Start building a new {@link StringRequest} instance.
         */
        public Builder(String url) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Url must not be empty.");
            }
            this.url = url;
        }

        public Builder method(Integer method) {
            this.method = method;
            return this;
        }

        public Builder responseListener(ResponseListener responseListener) {
            this.responseListener = responseListener;
            return this;
        }

        public Builder requestData(Object requestData) {
            this.requestData = requestData;
            return this;
        }

        public Builder cookie(Object cookie) {
            this.cookie = cookie;
            return this;
        }


        /**
         * Create the {@link ZctNetwork} instance.
         */
        public StringRequest build() {

            if (method == null) {
                method = DEFAULT_METHOD;
            }

            return new StringRequest(this);
        }
    }
}
