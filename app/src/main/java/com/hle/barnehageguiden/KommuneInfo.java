package com.hle.barnehageguiden;

import org.json.JSONObject;

import java.io.Serializable;

public class KommuneInfo implements Serializable {


    protected String kommuneInfoNavn;
    protected String kommuneInfoNr;

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

    public KommuneInfo (JSONObject jsonKommuneInfo){
        this.antallBarn = jsonKommuneInfo.optInt(KOL_ANTALLBARN);
        this.barnPrAnsatt = jsonKommuneInfo.optDouble(KOL_BARNPRANSATT);
        this.andelUtdannet = jsonKommuneInfo.optDouble(KOL_ANDELUTDANNET);
        this.arealPrBarn = jsonKommuneInfo.optDouble(KOL_AREALPRBARN);
        this.andelNormOk = jsonKommuneInfo.optDouble(KOL_ANDELNORMOK);
    }

    public void setKommuneInfoNavn(String kommuneInfoNavn) {
        this.kommuneInfoNavn = kommuneInfoNavn;
    }

    public void setKommuneInfoNr(String kommuneInfoNr) {
        this.kommuneInfoNr = kommuneInfoNr;
    }

}
