package com.zerocodeteam.networktestapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zerocodeteam.network.ZctNetwork;

/**
 * Created by rade on 11/09/16.
 */
public class Example {


    @SerializedName("one")
    @Expose
    private String one = "one";
    @SerializedName("key")
    @Expose
    private String key = "key";

    /**
     * @return The one
     */
    public String getOne() {
        return one;
    }

    /**
     * @param one The one
     */
    public void setOne(String one) {
        this.one = one;
    }

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return App.getNetwork().getGson().toJson(this);
    }
}
