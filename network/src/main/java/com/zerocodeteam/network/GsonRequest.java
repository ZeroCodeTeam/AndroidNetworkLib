package com.zerocodeteam.network;

import android.text.TextUtils;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class GsonRequest<T> extends NetworkRequest<T> {

    private GsonRequest(Builder builder) {
        super(builder.method, builder.url, builder.responseListener, builder.clazz, builder.requestData, builder.cookie);
    }

    /**
     * Fluent API for creating {@link GsonRequest} instances.
     */
    public static class Builder<T> {
        private Integer DEFAULT_METHOD = Method.GET;
        private Class DEFAULT_CLASS = Object.class;

        private String url;
        private Class<T> clazz;
        private Integer method;
        private ResponseListener<T> responseListener;
        private Object requestData;
        private Object cookie;


        /**
         * Start building a new {@link GsonRequest} instance.
         */
        public Builder(String url) {
            if (TextUtils.isEmpty(url)) {
                throw new IllegalArgumentException("Url must not be empty.");
            }
            this.url = url;
        }

        public Builder responseClazz(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder method(Integer method) {
            this.method = method;
            return this;
        }

        public Builder responseListener(ResponseListener<T> responseListener) {
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
        public GsonRequest build() {

            if (method == null) {
                method = DEFAULT_METHOD;
            }

            if (clazz == null) {
                clazz = DEFAULT_CLASS;
            }

            return new GsonRequest(this);
        }
    }
}
