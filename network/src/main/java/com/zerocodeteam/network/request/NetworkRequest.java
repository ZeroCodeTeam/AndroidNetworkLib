package com.zerocodeteam.network.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public abstract class NetworkRequest<T> extends Request<T> {

    private final Map<String, String> mHeaders;
    private StringEntity mStringEntity;
    protected Map<String, String> mResponseHeaders;
    private final ResponseListener mSuccessListener;
    protected final Object mCookie;

    public NetworkRequest(Object cookie, int method, String url, Map<String, String> headers, StringEntity stringEntity, ResponseListener successListener) {
        super(method, url, null);
        this.mHeaders = headers;
        this.mStringEntity = stringEntity;
        this.mSuccessListener = successListener;
        this.mCookie = cookie;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mSuccessListener != null) {
            mSuccessListener.onResponseSuccess(mCookie, response, mResponseHeaders);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        if (mSuccessListener != null) {
            mSuccessListener.onErrorResponse(mCookie, error);
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

    public interface ResponseListener<T> {
        void onResponseSuccess(Object cookie, T createdObject, Map<String, String> responseHeaders);

        void onErrorResponse(Object cookie, VolleyError error);
    }
}
