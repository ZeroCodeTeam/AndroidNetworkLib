package com.zerocodeteam.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Bane on 26.7.2015.
 */
public abstract class NetworkRequest<T> extends Request<T> {

    private final Map<String, String> mHeaders;
    private StringEntity mStringEntity;
    protected Map<String, String> mResponseHeaders;
    protected final Object mCookie;
    private Object mMetaData;

    private final Response.Listener<T> mListener = new Response.Listener<T>() {
        @Override
        public void onResponse(T t) {
            if (mSuccessListener != null) {
                if (mMetaData != null) {
                    mSuccessListener.onResponseSuccess(mCookie, t, mResponseHeaders, mMetaData);
                } else {
                    mSuccessListener.onResponseSuccess(mCookie, t, mResponseHeaders);
                }
            }
        }
    };

    private final ResponseListener mSuccessListener;

    public interface ResponseListener<T> {
        void onResponseSuccess(Object cookie, T createdObject, Map<String, String> responseHeaders, Object... metaData);

        void onErrorResponse(VolleyError error, Object... metaData);
    }

    public NetworkRequest(Object cookie, int method, String url, Map<String, String> headers, StringEntity stringEntity, ResponseListener successListener, Response.ErrorListener errorListener, Object... metaData) {
        super(method, url, errorListener);
        this.mHeaders = headers;
        this.mStringEntity = stringEntity;
        this.mSuccessListener = successListener;
        this.mCookie = cookie;
        this.mMetaData = metaData.length != 0 ? metaData[0] : null;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (mSuccessListener != null && mMetaData != null) {
            mSuccessListener.onErrorResponse(error, mMetaData);
        }

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mStringEntity != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                mStringEntity.writeTo(outputStream);
            } catch (IOException e) {
                VolleyLog.e("IOException @ " + getClass().getSimpleName());
            }
            return outputStream.toByteArray();
        } else {
            return super.getBody();
        }
    }

    @Override
    public String getBodyContentType() {
        return mStringEntity != null ? mStringEntity.getContentType().getValue() : super.getBodyContentType();
    }
}
