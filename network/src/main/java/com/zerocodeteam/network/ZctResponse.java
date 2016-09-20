package com.zerocodeteam.network;

import com.android.volley.VolleyError;
import com.zerocodeteam.network.ZctNetwork.ErrorType;

import java.util.Map;

/**
 * Created by ZeroCodeTeam on 27/12/15.
 */
public interface ZctResponse<T> {

    void onSuccess(T responseObject, Map<String, String> responseHeaders, Object cookie);

    void onError(VolleyError error, ErrorType type, Map<String, String> responseHeaders, Object cookie);
}
