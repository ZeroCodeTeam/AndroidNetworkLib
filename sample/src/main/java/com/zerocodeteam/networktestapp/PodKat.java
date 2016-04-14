package com.zerocodeteam.networktestapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PodKat {

    @SerializedName("nazkat")
    @Expose
    private String nazkat;
    @SerializedName("sifkat")
    @Expose
    private String sifkat;

    /**
     * @return The nazkat
     */
    public String getNazkat() {
        return nazkat;
    }

    /**
     * @param nazkat The nazkat
     */
    public void setNazkat(String nazkat) {
        this.nazkat = nazkat;
    }

    /**
     * @return The sifkat
     */
    public String getSifkat() {
        return sifkat;
    }

    /**
     * @param sifkat The sifkat
     */
    public void setSifkat(String sifkat) {
        this.sifkat = sifkat;
    }

}
