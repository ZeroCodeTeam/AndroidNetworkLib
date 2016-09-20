package com.zerocodeteam.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctNetwork {

    private static final String DEFAULT_TAG = ZctNetwork.class.getSimpleName();
    private static Boolean mLoggingEnabled;

    /**
     * Singleton objects
     */
    private static ZctNetwork sInstance = null;
    private static Gson sGson;

    /**
     * Queue of network requests
     */
    private Integer mRequestTimeout;
    private String mRequestTag;
    private RequestQueue mRequestQueue;

    private ZctNetwork(Builder builder) {
        this.mRequestTimeout = builder.requestTimeout;
        this.mRequestTag = builder.requestTag;
        this.mLoggingEnabled = builder.loggingEnabled;
        this.mRequestQueue = Volley.newRequestQueue(builder.context.getApplicationContext());

        ZctNetwork.log(ZctNetwork.class.getSimpleName() + " object created");
    }

    /**
     * Get singleton instance of ZctNetwork object.
     *
     * @return - Instance of ZctNetwork object.
     */
    protected static ZctNetwork getInstance() {
        return sInstance;
    }

    /**
     * Log all network lib activities if it is enabled by user.
     */
    protected static void log(String msg) {
        if (ZctNetwork.mLoggingEnabled) {
            Log.e(DEFAULT_TAG, msg);
        }
    }

    /**
     * Default values for ZctNetwork object:
     * timeout:             2500 ms
     * dialog min time:     0 ms
     * request tag:         "ZctNetwork"
     * logging enabled:     false
     *
     * @param context - Associated context to this instance.
     * @return - Instance of ZctNetwork object.
     */
    public static ZctNetwork with(Context context) {
        if (sInstance == null) {
            synchronized (ZctNetwork.class) {
                if (sInstance == null) {
                    sInstance = new Builder(context).build();
                }
            }
        }

        return sInstance;
    }

    /**
     * Obtain singleton GSON instance
     *
     * @return GSON instance
     */
    public static Gson getGsonInstance() {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson;
    }

    /**
     * Checks if there is network connectivity
     *
     * @return TRUE - connected, FALSE - not
     */
    public static NetworkType isDeviceOnline(Context context) {

        NetworkType ret = NetworkType.NO_NETWORK;

        if (Connectivity.isConnectedWifi(context)) {
            ret = NetworkType.WIFI;
        }

        if (Connectivity.isConnectedMobile(context)) {
            if (ret != NetworkType.NO_NETWORK) {
                ret = NetworkType.WIFI_AND_MOBILE;
            } else {
                ret = NetworkType.MOBILE;
            }
        }

        return ret;
    }


    /**
     * Add new request to request queue and start fetching from network.
     *
     * @param req - Network request that should be executed.
     * @param <T> - Generic request object.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public <T> void sendRequest(NetworkRequest<T> req) throws IllegalStateException {

        if (req == null) {
            ZctNetwork.log("Received request is null");
            return;
        }

        if (mRequestQueue == null) {
            ZctNetwork.log("Object not initialized, please review your code.");
            throw new IllegalStateException("Object not initialized, please review your code.");
        }

        req.setRetryPolicy(new DefaultRetryPolicy(mRequestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ZctNetwork.log(req.toString());

        req.setTag(mRequestTag);
        mRequestQueue.add(req);
        ZctNetwork.log("Request added to queue");
    }

    /**
     * Cancel network requests with specific tag.
     *
     * @param tag - Tag that uniquely identify network request.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public void cancelRequestsByTag(final String tag) throws IllegalStateException {

        if (mRequestQueue == null) {
            ZctNetwork.log("Object not initialized, please review your code.");
            throw new IllegalStateException("Object not initialized, please review your code.");
        }

        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                if (request.getTag().equals(tag)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Cancel all network requests.
     *
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public void cancelAllRequests() throws IllegalStateException {

        if (mRequestQueue == null) {
            ZctNetwork.log("Object not initialized, please review your code.");
            throw new IllegalStateException("Object not initialized, please review your code.");
        }

        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public enum ErrorType {
        TIMEOUT("TIMEOUT"),
        AUTH_FAILURE("AUTH_FAILURE"),
        SERVER_ERROR("SERVER_ERROR"),
        NETWORK_ERROR("NETWORK_ERROR"),
        PARSE_ERROR("PARSE_ERROR"),
        UNKNOWN_ERROR("UNKNOWN_ERROR");

        private final String name;

        private ErrorType(String s) {
            name = s;
        }

        public boolean equals(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum NetworkType {
        NO_NETWORK("NO NETWORK"),
        WIFI("WIFI"),
        MOBILE("MOBILE"),
        WIFI_AND_MOBILE("WIFI_AND_MOBILE");

        private final String name;

        private NetworkType(String s) {
            name = s;
        }

        public boolean equals(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Fluent API for creating {@link ZctNetwork} instances.
     */
    public static class Builder {
        private static Boolean DEFAULT_LOGGING = false;

        private Context context = null;
        private String requestTag = null;
        private Boolean loggingEnabled = null;
        private Integer requestTimeout = null;

        /**
         * Start building a new {@link ZctNetwork} instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context;
        }

        /**
         * Specify timeout time for network request. If not set, default volley network timeout time is set.
         *
         * @param timeout - Network timeout time in milliseconds.
         * @return - Instance of Builder object.
         */
        public Builder defaultTimeout(Integer timeout) {
            this.requestTimeout = timeout;
            return this;
        }

        /**
         * Specify tag that is used by volley request.
         *
         * @param tag - Tag used for post-management of request.
         * @return - Instance of Builder object.
         */
        public Builder defaultRequestTag(String tag) {
            this.requestTag = tag;
            return this;
        }

        /**
         * Toggle whether debug logging is enabled.
         *
         * @param enabled - If enabled, network lib will trace it's activity to logcat.
         * @return - Instance of Builder object.
         */
        public Builder defaultLoggingEnabled(Boolean enabled) {
            this.loggingEnabled = enabled;
            return this;
        }

        /**
         * Create the {@link ZctNetwork} instance.
         */
        public ZctNetwork build() {

            if (requestTimeout == null) {
                requestTimeout = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
            }

            if (requestTag == null) {
                requestTag = DEFAULT_TAG;
            }

            if (loggingEnabled == null) {
                loggingEnabled = DEFAULT_LOGGING;
            }

            sInstance = new ZctNetwork(this);
            return sInstance;
        }
    }
}
