package com.hle.barnehageguiden;

import org.json.JSONObject;

import java.io.Serializable;

public class BarnehageInfo implements Serializable {


    protected String kommuneInfoNavn;
    protected String kommuneInfoNr;

    protected int antallBarn;
    protected double barnPrAnsatt;
    protected double andelUtdannet;
    protected double arealPrBarn;
    protected double andelNormOk;

    private static final String KOL_ANTALLBARN          = "antallBarn";
    private static final String KOL_BARNPRANSATT        = "antallBarnPerAnsatt";
    private static final String KOL_ANDELUTDANNET       = "andelAnsatteBarnehagelarer";
    private static final String KOL_AREALPRBARN         = "lekeOgOppholdsarealPerBarn";
    private static final String KOL_ANDELNORMOK         = "andelBarnehagerSomOppfyllerPedagognormen";

    public BarnehageInfo(JSONObject jsonBarnehageInfo){
        this.antallBarn = jsonBarnehageInfo.optInt(KOL_ANTALLBARN);
        this.barnPrAnsatt = jsonBarnehageInfo.optDouble(KOL_BARNPRANSATT);
        this.andelUtdannet = jsonBarnehageInfo.optDouble(KOL_ANDELUTDANNET);
        this.arealPrBarn = jsonBarnehageInfo.optDouble(KOL_AREALPRBARN);
        this.andelNormOk = jsonBarnehageInfo.optDouble(KOL_ANDELNORMOK);
    }

    public String getKommuneInfoNavn() {
        return kommuneInfoNavn;
    }

    public String getKommuneInfoNr() {
        return kommuneInfoNr;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public double getBarnPrAnsatt() {
        return barnPrAnsatt;
    }

    public double getAndelUtdannet() {
        return andelUtdannet;
    }

    public double getArealPrBarn() {
        return arealPrBarn;
    }

    public double getAndelNormOk() {
        return andelNormOk;
    }

    public void setKommuneInfoNavn(String kommuneInfoNavn) {
        this.kommuneInfoNavn = kommuneInfoNavn;
    }

    public void setKommuneInfoNr(String kommuneInfoNr) {
        this.kommuneInfoNr = kommuneInfoNr;
    }

}
