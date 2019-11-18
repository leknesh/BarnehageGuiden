package com.hle.barnehageguiden;

//API RequestURL: https://www.barnehagefakta.no/api/Location/kommune/ + kommuneNr

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Barnehage {

    protected String barnehageNavn;
    protected String barnehageNr;
    protected int antallBarn;
    protected String eierform;

    private static final String KOL_BARNEHAGENAVN       = "navn";
    private static final String KOL_BARNEHAGENR         = "nsrId";
    private static final String KOL_ANTALLBARN          = "antallBarn";
    private static final String KOL_EIERFORM            = "eierform";

    //JSON-konstruktør
    public Barnehage(JSONObject jsonBarnehage){
        this.barnehageNr    = jsonBarnehage.optString(KOL_BARNEHAGENR);
        this.barnehageNavn  = jsonBarnehage.optString(KOL_BARNEHAGENAVN);
        this.antallBarn     = jsonBarnehage.optInt(KOL_ANTALLBARN);
        this.eierform       = jsonBarnehage.optString(KOL_EIERFORM);
    }

    public String getBarnehageNavn() {
        return barnehageNavn;
    }

    public void setBarnehageNavn(String barnehageNavn) {
        this.barnehageNavn = barnehageNavn;
    }

    public String getBarnehageNr() {
        return barnehageNr;
    }

    public void setBarnehageNr(String barnehageNr) {
        this.barnehageNr = barnehageNr;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public void setAntallBarn(int antallBarn) {
        this.antallBarn = antallBarn;
    }

    public String getEierform() {
        return eierform;
    }

    public void setEierform(String eierform) {
        this.eierform = eierform;
    }

    @Override
    public String toString(){
        return "BarnehageNr: " + barnehageNr + ", barnehagenavn: " + barnehageNavn;
    }


}
