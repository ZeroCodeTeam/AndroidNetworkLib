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

    public static final String TAG = ZctNetwork.class.getSimpleName();
    /**
     * Time out request time, 60 seconds default
     */
    public static final int DEFAULT_TIMEOUT_MS = 60000;

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
    public void init(Context context) {
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
    public <T> void sendRequest(Request<T> req, String tag) {
        req.setShouldCache(false);
        req.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        if (req.getMethod() == Request.Method.PATCH) {
            mRequestQueueForPatchRequests.add(req);
        } else {
            mRequestQueue.add(req);
        }
    }

    /**
     * Add new request to request queue and start fetching from network
     */
    public <T> void sendRequest(Request<T> req) {
        req.setTag(TAG);
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

    /**
     * @return Returns request queue
     */
    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }
}
