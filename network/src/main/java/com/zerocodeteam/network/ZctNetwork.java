package com.zerocodeteam.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctNetwork {

    public static final String DEFAULT_TAG = ZctNetwork.class.getSimpleName();
    private static volatile ZctNetwork sInstance;
    private static volatile ZctNetworkUtils sUtilsInstance;
    private static Boolean mLoggingEnabled;

    /**
     * Queue of network requests
     */
    private RequestQueue mRequestQueue;
    private Context mContext;
    private Integer mRequestTimeout;
    private String mRequestTag;
    private Boolean mDialogEnabled;
    private String mDialogMsg;
    private ProgressDialog mProgressDialog;
    private Integer mMinDialogTime;

    private Long mCurTime;
    private Handler mUiHelper;


    private ZctNetwork(Builder builder) {
        this.mContext = builder.context;
        this.mRequestTimeout = builder.requestTimeout;
        this.mRequestTag = builder.requestTag;
        this.mDialogEnabled = builder.dialogEnabled;
        this.mDialogMsg = builder.dialogMsg;
        this.mMinDialogTime = builder.minDialogTime;
        this.mRequestQueue = Volley.newRequestQueue(builder.context.getApplicationContext());
        this.mUiHelper = new Handler();
        ZctNetwork.mLoggingEnabled = builder.loggingEnabled;
        log(ZctNetwork.class.getSimpleName() + " object created");
    }

    /**
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
            log("New context associated");
            sInstance.mContext = context;
        }
        return sInstance;
    }

    /**
     * Network helpers.
     *
     * @return - Singleton instance of ZctNetworkUtils.
     */
    public static ZctNetworkUtils getUtils() {
        if (sUtilsInstance == null) {
            synchronized (ZctNetworkUtils.class) {
                if (sUtilsInstance == null) {
                    sUtilsInstance = new ZctNetworkUtils();
                }
            }
        }
        return sUtilsInstance;
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
        if (mLoggingEnabled) {
            Log.e(DEFAULT_TAG, msg);
        }
    }

    /**
     * Add new request to request queue and start fetching from network.
     *
     * @param req - Network request that should be executed.
     * @param <T> - Generic request object.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public <T> void executeRequest(Request<T> req) throws IllegalStateException {
        executeRequest(req, false);
    }

    /**
     * Add new request to request queue and start fetching from network, without showing loading dialog.
     *
     * @param req - Network request that should be executed.
     * @param <T> - Generic request object.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public <T> void executeSilentRequest(Request<T> req) throws IllegalStateException {
        executeRequest(req, true);
    }

    /**
     * Add new request to request queue and start fetching from network.
     *
     * @param req - Network request that should be executed.
     * @param <T> - Generic request object.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    private <T> void executeRequest(Request<T> req, Boolean silent) throws IllegalStateException {

        if (!silent && mDialogEnabled) {
            showProgressDialog(mContext);
        }

        if (mRequestQueue == null) {
            log("Object not initialized, please review your code.");
            throw new IllegalStateException("Object not initialized, please review your code.");
        }

        req.setRetryPolicy(new DefaultRetryPolicy(mRequestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        req.setTag(mRequestTag);
        mRequestQueue.add(req);
        log("Request added to queue");
    }

    /**
     * Cancel network request with specific tag.
     *
     * @param tag - Tag that uniquely identify network request.
     * @throws IllegalStateException - Handle this exception if basic request queue is not initialized.
     */
    public void cancelRequests(final String tag) throws IllegalStateException {
        dismissProgressDialog();

        if (mRequestQueue == null) {
            log("Object not initialized, please review your code.");
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
            log("Object not initialized, please review your code.");
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
            log("Dialog already shown");
            return;
        }

        mProgressDialog = ProgressDialog.show(context, "", mDialogMsg, true);
        mCurTime = System.currentTimeMillis();
        log("Dialog shown");
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
                        mProgressDialog.dismiss();
                        log("Dialog dismissed with delay: " + (mMinDialogTime - timeDiff));
                    }
                }, mMinDialogTime - timeDiff);

            } else {
                mProgressDialog.dismiss();
                log("Dialog dismissed");
            }
        }
    }

    public enum ErrorType {
        TIMEOUT,
        AUTH_FAILURE,
        SERVER_ERROR,
        NETWORK_ERROR,
        PARSE_ERROR,
        UNKNOWN_ERROR
    }

    /**
     * Fluent API for creating {@link ZctNetwork} instances.
     */
    public static class Builder {
        private static Boolean DEFAULT_LOGGING = false;
        private static Boolean DEFAULT_DIALOG_ENABLED = true;
        private static String DEFAULT_DIALOG_MSG = "Loading. Please wait..";
        private static Integer DEFAULT_DIALOG_SHOWING_TIME = 500;

        private Context context;
        private String requestTag;
        private String dialogMsg;
        private Boolean loggingEnabled;
        private Boolean dialogEnabled;
        private Integer requestTimeout;
        private Integer minDialogTime;

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
