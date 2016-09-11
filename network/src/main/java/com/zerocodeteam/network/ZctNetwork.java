package com.zerocodeteam.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
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
    private Context mContext;
    private Integer mRequestTimeout;
    private String mRequestTag;
    private Boolean mDialogEnabled;
    private String mDialogMsg;
    private Integer mMinDialogTime;
    private RequestQueue mRequestQueue;
    private Handler mUiHelper;
    private ProgressDialog mProgressDialog;
    private Long mCurTime;

    private ZctNetwork(Builder builder) {
        this.mContext = builder.context;
        this.mRequestTimeout = builder.requestTimeout;
        this.mRequestTag = builder.requestTag;
        this.mDialogEnabled = builder.dialogEnabled;
        this.mDialogMsg = builder.dialogMsg;
        this.mMinDialogTime = builder.minDialogTime;
        this.mLoggingEnabled = builder.loggingEnabled;
        this.mRequestQueue = Volley.newRequestQueue(builder.context.getApplicationContext());
        this.mUiHelper = new Handler();

        ZctNetwork.log(ZctNetwork.class.getSimpleName() + " object created");
    }

    /**
     * Default values for ZctNetwork object:
     *
     * timeout:             2500ms
     * dialog min time:     500ms
     * request tag:         "ZctNetwork"
     * dialog enabled:      true
     * logging enabled:     false
     * default dialog msg:  "Loading. Please wait."
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
        } else {
            ZctNetwork.log("New context associated");
            sInstance.mContext = context;
        }
        return sInstance;
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
        sendRequest(req, false);
    }

    /**
     * Add new request to request queue and start fetching from network, without showing loading dialog.
     *
     * @param req - Network request that should be executed.
     * @param <T> - Generic request object.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public <T> void sendSilentRequest(NetworkRequest<T> req) throws IllegalStateException {
        sendRequest(req, true);
    }

    /**
     * Add new request to request queue and start fetching from network.
     *
     * @param req - Network request that should be executed.
     * @param <T> - Generic request object.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    private <T> void sendRequest(NetworkRequest<T> req, Boolean silent) throws IllegalStateException {

        String logRequest = "";
        if (req == null) {
            ZctNetwork.log("Received request is null");
            return;
        }

        if (!silent && mDialogEnabled) {
            showProgressDialog(mContext);
        }

        if (mRequestQueue == null) {
            ZctNetwork.log("Object not initialized, please review your code.");
            throw new IllegalStateException("Object not initialized, please review your code.");
        }

        req.setRetryPolicy(new DefaultRetryPolicy(mRequestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        if (req instanceof StringRequest) {
            logRequest = StringRequest.class.getSimpleName();
        } else if (req instanceof GsonRequest) {
            logRequest = GsonRequest.class.getSimpleName();
        }
        ZctNetwork.log(logRequest + "\n" + req.toString());

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
        dismissProgressDialog();

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
        dismissProgressDialog();

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

    /**
     * Show default progress bar. Used only by network lib.
     *
     * @param context - Associated context for progress dialog.
     */
    protected void showProgressDialog(Context context) {
        if (!mDialogEnabled) {
            return;
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            ZctNetwork.log("Dialog already shown");
            mCurTime = System.currentTimeMillis();
            return;
        }

        mProgressDialog = ProgressDialog.show(context, "", mDialogMsg, true);
        mCurTime = System.currentTimeMillis();
        ZctNetwork.log("Dialog shown");
    }

    /**
     * Hide default progress bar. Used only by network lib.
     */
    protected void dismissProgressDialog() {
        if (mDialogEnabled && mProgressDialog != null && mProgressDialog.isShowing()) {
            final Long timeDiff = System.currentTimeMillis() - mCurTime;
            if (timeDiff < mMinDialogTime) {
                mUiHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // It could happen that few callbacks are called in same time, so we need to ignore dialog hiding if ti already hidden.
                        if( mProgressDialog.isShowing() ) {
                            mProgressDialog.dismiss();
                            ZctNetwork.log("Dialog dismissed with delay: " + (mMinDialogTime - timeDiff));
                        }
                    }
                }, mMinDialogTime - timeDiff);

            } else {
                mProgressDialog.dismiss();
                ZctNetwork.log("Dialog dismissed");
            }
        }
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
        private static Boolean DEFAULT_DIALOG_ENABLED = true;
        private static String DEFAULT_DIALOG_MSG = "Loading. Please wait.";
        private static Integer DEFAULT_DIALOG_SHOWING_TIME = 500;

        private Context context = null;
        private String requestTag = null;
        private String dialogMsg = null;
        private Boolean loggingEnabled = null;
        private Boolean dialogEnabled = null;
        private Integer requestTimeout = null;
        private Integer minDialogTime = null;

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
         * Specify if dialog should be shown when network request is executed.
         *
         * @param enabled - True if dialog should be shown, otherwise false.
         * @return - Instance of Builder object.
         */
        public Builder defaultDialogEnable(Boolean enabled) {
            this.dialogEnabled = enabled;
            return this;
        }

        /**
         * Specify default message that will be shown in dialog while loading is in progress.
         *
         * @param msg - Message shown by dialog.
         * @return - Instance of Builder object.
         */
        public Builder defaultDialogMsg(String msg) {
            this.dialogMsg = msg;
            return this;
        }

        /**
         * Specify minimal time frame between dialog is shown and hidden.
         *
         * @param minDialogTime - Minimum dialog showing time.
         * @return - Instance of Builder object.
         */
        public Builder defaultMinDialogTime(Integer minDialogTime) {
            this.minDialogTime = minDialogTime;
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

            if (dialogEnabled == null) {
                dialogEnabled = DEFAULT_DIALOG_ENABLED;
            }

            if (dialogMsg == null) {
                dialogMsg = DEFAULT_DIALOG_MSG;
            }

            if (minDialogTime == null) {
                minDialogTime = DEFAULT_DIALOG_SHOWING_TIME;
            }

            if (loggingEnabled == null) {
                loggingEnabled = DEFAULT_LOGGING;
            }

            sInstance = new ZctNetwork(this);
            return sInstance;
        }
    }
}
