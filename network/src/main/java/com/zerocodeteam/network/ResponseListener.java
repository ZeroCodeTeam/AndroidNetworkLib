package com.zerocodeteam.network;

import com.android.volley.VolleyError;
import com.zerocodeteam.network.ZctNetwork.ErrorType;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 27/12/15.
 */
public interface ResponseListener<T> {

    void onResponseSuccess(T responseObject, Map<String, String> responseHeaders, Object cookie);

    void onErrorResponse(VolleyError error, ErrorType type, Map<String, String> responseHeaders, Object cookie);
}
