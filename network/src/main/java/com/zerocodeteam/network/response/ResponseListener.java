package com.zerocodeteam.network.response;

import com.android.volley.VolleyError;
import com.zerocodeteam.network.ZctNetwork.ErrorType;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 27/12/15.
 */
public interface ResponseListener<T> {

    void onResponseSuccess(Object cookie, T createdObject, Map<String, String> responseHeaders);

    void onErrorResponse(ErrorType type, Object cookie, VolleyError error, Map<String, String> responseHeaders);
}
