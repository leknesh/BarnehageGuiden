package com.hle.barnehageguiden;

//API RequestURL: https://data-nbr.udir.no/fylker

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Kommune implements Serializable {

    protected String kommuneNavn;
    protected String kommuneNr;

    private static final String KOL_KOMMUNENAVN       = "Navn";
    private static final String KOL_KOMMUNENR         = "Kommunenr";

    //JSON-konstruktør
    public Kommune(JSONObject jsonKommune){
        this.kommuneNr    = jsonKommune.optString(KOL_KOMMUNENR);
        this.kommuneNavn  = jsonKommune.optString(KOL_KOMMUNENAVN);
    }

    public String getKommuneNavn() {
        return kommuneNavn;
    }

    public void setKommuneNavn(String fylkeNavn) {
        this.kommuneNavn = kommuneNavn;
    }

    public String getKommuneNr() {
        return kommuneNr;
    }

    public void setKommuneNr(String kommuneNr) {
        this.kommuneNr = kommuneNr;
    }


    @Override
    public String toString(){
        return "kommuneNr: " + kommuneNr + ", kommunenavn: " + kommuneNavn;
    }


}
