package com.zerocodeteam.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by Rade on 06.12.2015.
 */
public class ZctNetwork {

    public static final String TAG = ZctNetwork.class.getSimpleName();

    /**
     * Queue of network requests
     */
    private static RequestQueue mRequestQueue;

    /**
     * Requests queue for PATCH requests
     */
    private static RequestQueue mRequestQueueForPatchRequests;

    /**
     * Client must first call this method after initializing call of getInstance() method.
     */
    public static void init(Context context) {
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
            Log.e(TAG, e.toString());
        }
        HttpStack httpStack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        mRequestQueueForPatchRequests = Volley.newRequestQueue(context.getApplicationContext(), httpStack);
    }

    /**
     * Add new request to request queue and start fetching from network
     */
    public static <T> void addRequest(Request<T> req, String tag) {
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
    public static <T> void addRequest(Request<T> req) {
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
    public static void cancelPendingRequests(final String tag) {
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
    public static void cancelAllPendingRequests() {
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
    public static RequestQueue getRequestQueue() {
        return ZctNetwork.mRequestQueue;
    }
}
