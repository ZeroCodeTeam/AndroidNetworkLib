package com.zerocodeteam.networktestapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Kategorije {

    @SerializedName("nazkat")
    @Expose
    private String nazkat;
    @SerializedName("sifkat")
    @Expose
    private String sifkat;
    @SerializedName("podKat")
    @Expose
    private List<PodKat> podKat = new ArrayList<PodKat>();

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

    /**
     * @return The podKat
     */
    public List<PodKat> getPodKat() {
        return podKat;
    }

    /**
     * @param podKat The podKat
     */
    public void setPodKat(List<PodKat> podKat) {
        this.podKat = podKat;
    }

}
