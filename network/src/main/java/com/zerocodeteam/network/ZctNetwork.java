package com.zerocodeteam.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class ZctNetwork {

    public static final String DEFAULT_REQUEST_TAG = ZctNetwork.class.getSimpleName();
    /**
     * Time out request time, 60 seconds default
     */
    public static int DEFAULT_TIMEOUT_MS;
    private static ZctNetwork sInstance;
    /**
     * Queue of network requests
     */
    private RequestQueue mRequestQueue;
    /**
     * Requests queue for PATCH requests
     */
    private RequestQueue mRequestQueueForPatchRequests;

    private ZctNetwork() {
    }

    public static ZctNetwork getInstance() {
        if (sInstance == null) {
            sInstance = new ZctNetwork();
        }
        return sInstance;
    }

    /**
     * Client must call this method to initialize environment.
     *
     * @param context - Application context
     */
    public void init(Context context) {
        init(context, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS);
    }

    /**
     * Client must call this method after initializing call of getInstance() method.
     *
     * @param context          - Application context
     * @param defaultTimeoutMS - Default request timeout in MS
     */
    public void init(Context context, int defaultTimeoutMS) {

        if (defaultTimeoutMS != 0) {
            DEFAULT_TIMEOUT_MS = defaultTimeoutMS;
        }

        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        /**
         * Workaround for volley don't handle PATCH requests by default
         */
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        HttpStack httpStack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        mRequestQueueForPatchRequests = Volley.newRequestQueue(context.getApplicationContext(), httpStack);
    }

    /**
     * Add new request to request queue and start fetching from network
     *
     * @param req - Network request that should be executed
     * @param <T> - Generic request object
     * @throws IllegalStateException - Force user to call init method first
     */
    public <T> void sendRequest(Request<T> req) throws IllegalStateException {
        try {
            sendRequest(req, DEFAULT_REQUEST_TAG);
        } catch (IllegalStateException ise) {
            throw ise;
        }
    }

    /**
     * Add new request to request queue and start fetching from network
     *
     * @param req - Network request that should be executed
     * @param tag - Tag that uniquely identify network request
     * @param <T> - Generic request object
     * @throws IllegalStateException - Force user to call init method first
     */
    public <T> void sendRequest(Request<T> req, String tag) throws IllegalStateException {

        if (mRequestQueue == null || mRequestQueueForPatchRequests == null) {
            throw new IllegalStateException("Object not initialized, please call init() method first.");
        }

        // req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // set the default tag if tag is empty
        if (!TextUtils.isEmpty(tag)) {
            req.setTag(tag);
        } else {
            req.setTag(DEFAULT_REQUEST_TAG);
        }

        if (req.getMethod() == Request.Method.PATCH) {
            mRequestQueueForPatchRequests.add(req);
        } else {
            mRequestQueue.add(req);
        }
    }

    /**
     * Cancel network request with following tag
     *
     * @param tag - Tag that uniquely identify network request
     * @throws IllegalStateException - Force user to call init method first
     */
    public void cancelRequests(final String tag) throws IllegalStateException {

        if (mRequestQueue == null || mRequestQueueForPatchRequests == null) {
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

        mRequestQueueForPatchRequests.cancelAll(new RequestQueue.RequestFilter() {
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
        if (mRequestQueue == null || mRequestQueueForPatchRequests == null) {
            throw new IllegalStateException("Object not initialized, please call init() method first.");
        }
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

        mRequestQueueForPatchRequests.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public enum ErrorType {
        TIMEOUT,
        AUTH_FAILURE,
        SERVER_ERROR,
        NETWORK_ERROR,
        PARSE_ERROR,
        UNKNOWN_ERROR
    }
}
