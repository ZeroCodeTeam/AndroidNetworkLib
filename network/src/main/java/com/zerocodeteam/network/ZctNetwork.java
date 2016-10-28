package com.zerocodeteam.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;

import java.io.File;


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
    private ImageLoader mImageLoader;
    private LruCache<String, Bitmap> mCache;
    private ImageCache mImageLoaderCache;
    private int mErrorImage;
    private int mDefaultImage;

    private ZctNetwork(Builder builder) {

        this.mErrorImage = builder.errorResource;
        this.mDefaultImage = builder.defaultResource;
        this.mRequestTimeout = builder.requestTimeout;
        this.mRequestTag = builder.requestTag;
        this.mLoggingEnabled = builder.loggingEnabled;

        File cacheDir = new File(builder.context.getCacheDir(), builder.cacheDir);

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);

        this.mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir, builder.cacheSize), network);
        this.mRequestQueue.start();

        this.mCache = new LruCache<>(builder.maxImageCacheEntries);
        this.mImageLoaderCache = new ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        };
        this.mImageLoader = new ImageLoader(mRequestQueue, mImageLoaderCache);
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

    public void loadNetworkImage(String url, NetworkImageView networkImageView) {
        networkImageView.setImageUrl(url, mImageLoader);
    }

    public void loadImage(String url, ImageView imageView) {
        mImageLoader.get(url, ImageLoader.getImageListener(imageView,
                mDefaultImage, mErrorImage));
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
        private static Integer DEFAULT_MAX_IMAGE_CACHE_SIZE = 30;
        private static String DEFAULT_CACHE_DIR = "zctNetwork";
        private static int DEFAULT_NETWORK_CACHE_SIZE = 10 * 1024 * 1024; // 10Mb

        public String cacheDir = null;
        public int cacheSize = 0;
        private Context context = null;
        private String requestTag = null;
        private Boolean loggingEnabled = null;
        private Integer maxImageCacheEntries = null;
        private Integer requestTimeout = null;
        private int defaultResource = 0;
        private int errorResource = 0;


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
         * Configure default max image cache size value.
         *
         * @param maxEntries - Maximum number of entries in the cache. For all other caches,
         *                   this is the maximum sum of the sizes of the entries in this cache.
         * @return - Instance of Builder object.
         */
        public Builder defaultImageCacheEntries(Integer maxEntries) {
            this.maxImageCacheEntries = maxEntries;
            return this;
        }

        /**
         * Configure default image loader resources.
         *
         * @param defaultResource - Loading image is shown while image is loading.
         * @param errorResource   - Error image is shown if error occurs while loading image.
         * @return - Instance of Builder object.
         */
        public Builder defaultImageLoaderResources(int defaultResource, int errorResource) {
            this.defaultResource = defaultResource;
            this.errorResource = errorResource;
            return this;
        }

        /**
         * Configure default cache dir name.
         *
         * @param cacheDir - Cache dir name.
         * @return - Instance of Builder object.
         */
        public Builder defaultCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        /**
         * Configure cache size for network library.
         *
         * @param cacheSize - The maximum size of the cache in bytes.
         */
        public Builder defaultCacheSize(int cacheSize) {
            this.cacheSize = cacheSize;
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
            if (maxImageCacheEntries == null) {
                maxImageCacheEntries = DEFAULT_MAX_IMAGE_CACHE_SIZE;
            }
            if (errorResource == 0) {
                errorResource = android.R.drawable.ic_dialog_alert;
            }

            if (defaultResource == 0) {
                defaultResource = R.drawable.ic_default;
            }
            if (cacheDir == null) {
                cacheDir = DEFAULT_CACHE_DIR;
            }

            if (cacheSize == 0) {
                cacheSize = DEFAULT_NETWORK_CACHE_SIZE;
            }

            sInstance = new ZctNetwork(this);
            return sInstance;
        }

    }
}
