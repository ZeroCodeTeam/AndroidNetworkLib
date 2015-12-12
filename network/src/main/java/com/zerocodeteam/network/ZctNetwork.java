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

    public enum ErrorType {
        TIMEOUT,
        AUTH_FAILURE,
        SERVER_ERROR,
        NETWORK_ERROR,
        PARSE_ERROR,
        UNKNOWN_ERROR
    }

    public static final String DEFAULT_REQUEST_TAG = ZctNetwork.class.getSimpleName();
    /**
     * Time out request time, 60 seconds default
     */
    public static int DEFAULT_TIMEOUT_MS = 60000;

    private static ZctNetwork sInstance;

    /**
     * Queue of network requests
     */
    private RequestQueue mRequestQueue;

    /**
     * Requests queue for PATCH requests
     */
    private RequestQueue mRequestQueueForPatchRequests;

    public static ZctNetwork getInstance() {
        if (sInstance == null) {
            sInstance = new ZctNetwork();
        }
        return sInstance;
    }

    private ZctNetwork() {
    }

    /**
     * Client must call this method after initializing call of getInstance() method.
     */
    public void init(Context context, int... defaultTimeoutMS) {

        if (defaultTimeoutMS.length != 0) {
            DEFAULT_TIMEOUT_MS = defaultTimeoutMS[0];
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
     */
    public <T> void sendRequest(Request<T> req, String... tag) {

        // req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // set the default tag if tag is empty
        if (tag.length != 0) {
            req.setTag(TextUtils.isEmpty(tag[0]) ? DEFAULT_REQUEST_TAG : tag[0]);
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
     * Cancel network request
     */
    public void cancelRequests(final String tag) {
        if (mRequestQueue != null) {
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
    }

    /**
     * Cancel all network requests
     */
    public void cancelAllRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {

                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }
}
