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
    private long MIN_DIALOG_TIME = 500;
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

    private Long mCurTime;
    private Handler mUiHelper;


    private ZctNetwork(Builder builder) {
        this.mContext = builder.context;
        this.mRequestTimeout = builder.requestTimeout;
        this.mRequestTag = builder.requestTag;
        this.mDialogEnabled = builder.dialogEnabled;
        this.mDialogMsg = builder.dialogMsg;
        this.mRequestQueue = Volley.newRequestQueue(builder.context.getApplicationContext());
        this.mUiHelper = new Handler();
        mLoggingEnabled = builder.loggingEnabled;
        log(ZctNetwork.class.getSimpleName() + " object created");
    }

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

    protected static ZctNetwork getInstance() {
        return sInstance;
    }

    protected static void log(String msg) {
        if (mLoggingEnabled) {
            Log.e(DEFAULT_TAG, msg);
        }
    }

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
     * Add new request to request queue and start fetching from network
     *
     * @param req - Network request that should be executed
     * @param <T> - Generic request object
     * @throws IllegalStateException - Force user to call init method first
     */
    public <T> void executeRequest(Request<T> req) throws IllegalStateException {
        executeRequest(req, false);

    }

    /**
     * Add new request to request queue and start fetching from network
     *
     * @param req - Network request that should be executed
     * @param <T> - Generic request object
     * @throws IllegalStateException - Force user to call init method first
     */
    public <T> void executeRequest(Request<T> req, Boolean silent) throws IllegalStateException {

        if (!silent && mDialogEnabled) {
            showProgressDialog(mContext);
        }

        if (mRequestQueue == null) {
            log("Object not initialized, please call init() method first.");
            throw new IllegalStateException("Object not initialized, please call init() method first.");
        }

        req.setRetryPolicy(new DefaultRetryPolicy(mRequestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        req.setTag(mRequestTag);
        mRequestQueue.add(req);
        log("Request added to queue");
    }

    /**
     * Cancel network request with following tag
     *
     * @param tag - Tag that uniquely identify network request
     * @throws IllegalStateException - Force user to call init method first
     */
    public void cancelRequests(final String tag) throws IllegalStateException {
        dismissProgressDialog();

        if (mRequestQueue == null) {
            throw new IllegalStateException("Object not initialized, please call init() method first.");
        }

        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                if (((String) request.getTag()).equals(tag)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Cancel all network requests
     *
     * @throws IllegalStateException - Force user to call init method first
     */
    public void cancelAllRequests() throws IllegalStateException {
        dismissProgressDialog();

        if (mRequestQueue == null) {
            throw new IllegalStateException("Object not initialized, please call init() method first.");
        }
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

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

    protected void dismissProgressDialog() {
        if (mDialogEnabled && mProgressDialog != null && mProgressDialog.isShowing()) {
            final Long timeDiff = System.currentTimeMillis() - mCurTime;
            if (timeDiff < MIN_DIALOG_TIME) {
                mUiHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        log("Dialog dismissed with delay: " + (MIN_DIALOG_TIME - timeDiff));
                    }
                }, MIN_DIALOG_TIME - timeDiff);

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
        private static String DEFAULT_DIALOG_MSG = "Loading, please wait..";

        private Context context;
        private String requestTag;
        private String dialogMsg;
        private Boolean loggingEnabled;
        private Boolean dialogEnabled;
        private Integer requestTimeout;

        /**
         * Start building a new {@link ZctNetwork} instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context;
        }

        public Builder defaultTimeout(Integer timeout) {
            this.requestTimeout = timeout;
            return this;
        }

        public Builder defaultRequestTag(String tag) {
            this.requestTag = tag;
            return this;
        }

        public Builder defaultDialogEnable(Boolean enabled) {
            this.dialogEnabled = enabled;
            return this;
        }

        public Builder defaultDialogMsg(String msg) {
            this.dialogMsg = msg;
            return this;
        }

        /**
         * Toggle whether debug logging is enabled.
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

            if (loggingEnabled == null) {
                loggingEnabled = DEFAULT_LOGGING;
            }

            sInstance = new ZctNetwork(this);
            return sInstance;
        }
    }
}
