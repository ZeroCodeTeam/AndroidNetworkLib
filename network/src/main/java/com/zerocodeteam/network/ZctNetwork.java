package com.zerocodeteam.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctNetwork {

    private static final String LIB_NAME = ZctNetwork.class.getSimpleName();

    /**
     * Singleton objects
     */
    private static Gson sGson = null;
    private static Boolean sDebuggingEnabled;

    /**
     * Queue of network requests
     */
    private Context mContext;
    private RequestQueue mRequestQueue;
    private Integer mRequestTimeout;
    private Integer mRequestMaxRetries;
    private String mRequestTagHandle;

    private ZctNetwork(Builder builder) {

        this.mContext = builder.context;
        this.mRequestTimeout = builder.requestTimeoutMs;
        this.mRequestMaxRetries = builder.requestMaxRetries;
        this.mRequestTagHandle = builder.requestTagHandle;
        this.sDebuggingEnabled = builder.debuggingEnabled;

        File cacheDir = new File(mContext.getCacheDir(), LIB_NAME);
        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);

        this.mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir, builder.cacheSize), network);
        this.mRequestQueue.start();

        ZctNetwork.log(LIB_NAME + " created\n" +
                builder.toString());
    }

    /**
     * Log all network lib activities if it is enabled by user.
     *
     * @param msg - Log message.
     */
    protected static void log(String msg) {
        if (ZctNetwork.sDebuggingEnabled) {
            Log.e(LIB_NAME, msg);
        }
    }

    /**
     * Obtain singleton GSON instance.
     *
     * @return GSON instance.
     */
    public static Gson getGson() {
        if (sGson == null) {
            sGson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        }
        return sGson;
    }

    /**
     * Determine network traffic based on network type. It returns number of bytes transferred over
     * specific interface since last boot time.
     *
     * @param statsType - Interested network interface.
     * @return - Number of read bytes.
     */
    public Long readNetworkStats(NetworkStats statsType) {
        Long byteCount = -1L;
        String WIFI_RX = "/sys/class/net/wlan0/statistics/rx_bytes";
        String WIFI_TX = "/sys/class/net/wlan0/statistics/tx_bytes";
        String MOBILE_RX = "/sys/class/net/rmnet0/statistics/rx_bytes";
        String MOBILE_TX = "/sys/class/net/rmnet0/statistics/tx_bytes";

        switch (statsType) {
            case WIFI_RX:
                byteCount = readFileValue(WIFI_RX);
                break;
            case WIFI_TX:
                byteCount = readFileValue(WIFI_TX);
                break;
            case MOBILE_RX:
                byteCount = readFileValue(MOBILE_RX);
                break;
            case MOBILE_TX:
                byteCount = readFileValue(MOBILE_TX);
                break;
        }
        this.log("readNetworkStats: [TYPE : " + statsType + "] [BYTES COUNT: " + byteCount + "]");
        return byteCount;
    }

    /**
     * Checks if there is network connectivity. Response is status that says
     * if there is network connection and which type.
     *
     * @return NetworkType -  network connection type
     */
    public NetworkType isDeviceOnline() {
        NetworkType ret = NetworkType.NO_NETWORK;

        if (Connectivity.isConnectedWifi(mContext)) {
            ret = NetworkType.WIFI;
        }

        if (Connectivity.isConnectedMobile(mContext)) {
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
    public <T> void sendRequest(ZctRequest<T> req) throws IllegalStateException {
        if (req == null) {
            ZctNetwork.log("Received request is null");
            return;
        }

        if (mRequestQueue == null) {
            ZctNetwork.log("Object not initialized, please review your code.");
            throw new IllegalStateException("Object not initialized, please review your code.");
        }

        req.setTag(mRequestTagHandle);

        req.setRetryPolicy(new DefaultRetryPolicy(mRequestTimeout,
                mRequestMaxRetries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(req);
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

    private Long readFileValue(String fileName) {
        File file = new File(fileName);
        BufferedReader br = null;
        Long bytes = 0L;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            line = br.readLine();
            bytes = Long.parseLong(line);
        } catch (Exception e) {
            ZctNetwork.log(e.toString());
            return 0L;
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    ZctNetwork.log(e.toString());
                }
        }
        return bytes;
    }

    public enum ErrorType {
        TIMEOUT("TIMEOUT"),
        AUTH_FAILURE("AUTH_FAILURE"),
        SERVER_ERROR("SERVER_ERROR"),
        NETWORK_ERROR("NETWORK_ERROR"),
        PARSE_ERROR("PARSE_ERROR"),
        UNKNOWN_ERROR("UNKNOWN_ERROR");

        private final String name;

        ErrorType(String s) {
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

        NetworkType(String s) {
            name = s;
        }

        public boolean equals(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public enum NetworkStats {
        WIFI_RX("WIFI_RX"),
        WIFI_TX("WIFI_TX"),
        MOBILE_RX("MOBILE_RX"),
        MOBILE_TX("MOBILE_TX");

        private final String name;

        NetworkStats(String s) {
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
     * Fluent API for creating ZctNetwork instances.
     * <p>
     * Default values for ZctNetwork object:
     * timeout:             2500 ms
     * max retries:         1
     * request tag handle:  "zctNetwork"
     * logging enabled:     false
     * cache size:          10Mb
     */
    public static class Builder {
        private static Boolean DEFAULT_LOGGING = false;
        private static Integer DEFAULT_NETWORK_CACHE_SIZE = 10 * 1024 * 1024; // 10Mb

        private Context context = null;
        private Integer requestTimeoutMs = null;
        private Integer requestMaxRetries = null;
        private String requestTagHandle = null;
        private Boolean debuggingEnabled = null;
        private Integer cacheSize = null;

        /**
         * Start building a new {@link ZctNetwork} instance.
         *
         * @param context - Associated context to this instance.
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
         * @param timeoutMs - Network timeout time in milliseconds.
         * @return - Instance of Builder object.
         */
        public Builder requestTimeout(Integer timeoutMs) {
            this.requestTimeoutMs = timeoutMs;
            return this;
        }

        /**
         * Change default retry policy number from 1 to maxRetries.
         *
         * @param maxRetries - The maximum number of retries.
         * @return - Instance of Builder object.
         */
        public Builder reqeustMaxRetries(Integer maxRetries) {
            this.requestMaxRetries = maxRetries;
            return this;
        }

        /**
         * Specify tag that is used by volley request.
         *
         * @param tagHandle - Tag used for post-management of request.
         * @return - Instance of Builder object.
         */
        public Builder requestTagHandle(String tagHandle) {
            this.requestTagHandle = tagHandle;
            return this;
        }

        /**
         * Enable ZCT Network request traces. It will be visible under android logcat.
         *
         * @return - Instance of Builder object.
         */
        public Builder enableConsoleDebugging() {
            this.debuggingEnabled = true;
            return this;
        }

        /**
         * Configure cache size for network library.
         *
         * @param cacheSize - The maximum size of the cache in bytes.
         */
        public Builder cacheSize(Integer cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        /**
         * Create the {@link ZctNetwork} instance.
         */
        public ZctNetwork build() {

            if (requestTimeoutMs == null) {
                requestTimeoutMs = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
            }

            if (requestMaxRetries == null) {
                requestMaxRetries = DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
            }

            if (requestTagHandle == null) {
                requestTagHandle = LIB_NAME;
            }

            if (debuggingEnabled == null) {
                debuggingEnabled = DEFAULT_LOGGING;
            }

            if (cacheSize == null) {
                cacheSize = DEFAULT_NETWORK_CACHE_SIZE;
            }

            return new ZctNetwork(this);
        }

        @Override
        public String toString() {
            return "context=" + context +
                    ",\nrequestTimeoutMs=" + requestTimeoutMs +
                    ",\nrequestMaxRetries=" + requestMaxRetries +
                    ",\nrequestTagHandle='" + requestTagHandle + '\'' +
                    ",\ndebuggingEnabled=" + debuggingEnabled +
                    ",\ncacheSize=" + cacheSize + " b";
        }
    }
}
