package com.zerocodeteam.networktestapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Example {

    @SerializedName("ukupnoRecepata")
    @Expose
    private Integer ukupnoRecepata;
    @SerializedName("ukupnoOmiljenihRecepata")
    @Expose
    private Integer ukupnoOmiljenihRecepata;
    @SerializedName("Kategorije")
    @Expose
    private List<Kategorije> mKategorije = new ArrayList<Kategorije>();

    /**
     * @return The ukupnoRecepata
     */
    public Integer getUkupnoRecepata() {
        return ukupnoRecepata;
    }

    /**
     * @param ukupnoRecepata The ukupnoRecepata
     */
    public void setUkupnoRecepata(Integer ukupnoRecepata) {
        this.ukupnoRecepata = ukupnoRecepata;
    }

    /**
     * @return The ukupnoOmiljenihRecepata
     */
    public Integer getUkupnoOmiljenihRecepata() {
        return ukupnoOmiljenihRecepata;
    }

    /**
     * @param ukupnoOmiljenihRecepata The ukupnoOmiljenihRecepata
     */
    public void setUkupnoOmiljenihRecepata(Integer ukupnoOmiljenihRecepata) {
        this.ukupnoOmiljenihRecepata = ukupnoOmiljenihRecepata;
    }

    /**
     * @return The Kategorije
     */
    public List<Kategorije> getKategorije() {
        return mKategorije;
    }

    /**
     * @param Kategorije The Kategorije
     */
    public void setKategorije(List<Kategorije> Kategorije) {
        this.mKategorije = Kategorije;
    }

}
