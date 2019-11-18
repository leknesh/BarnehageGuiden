package com.hle.barnehageguiden;

import org.json.JSONObject;

class BarnehageDetalj {
    private String barnehageNavn;
    private String gateAdresse;
    private String postNr;
    private String postSted;
    private String telefonNr;
    private String mailAdresse;
    private String nettAdresse;
    private double[] koordinater;

    private static final String KOL_NAVN = "navn";

    public BarnehageDetalj(){}

    public BarnehageDetalj(JSONObject jsonBarnehageDetalj){

        this.barnehageNavn = jsonBarnehageDetalj.optString(KOL_NAVN);

    }

    public String getBarnehageNavn() {
        return barnehageNavn;
    }

    public String getGateAdresse() {
        return gateAdresse;
    }

    public String getPostNr() {
        return postNr;
    }

    public String getPostSted() {
        return postSted;
    }

    public String getTelefonNr() {
        return telefonNr;
    }

    public String getMailAdresse() {
        return mailAdresse;
    }

    public String getNettAdresse() {
        return nettAdresse;
    }

    public double[] getKoordinater() {
        return koordinater;
    }

    public void setBarnehageNavn(String barnehageNavn) {
        this.barnehageNavn = barnehageNavn;
    }

    public void setGateAdresse(String gateAdresse) {
        this.gateAdresse = gateAdresse;
    }

    public void setPostNr(String postNr) {
        this.postNr = postNr;
    }

    public void setPostSted(String postSted) {
        this.postSted = postSted;
    }

    public void setTelefonNr(String telefonNr) {
        this.telefonNr = telefonNr;
    }

    public void setMailAdresse(String mailAdresse) {
        this.mailAdresse = mailAdresse;
    }

    public void setNettAdresse(String nettAdresse) {
        this.nettAdresse = nettAdresse;
    }

    public void setKoordinater(double[] koordinater) {
        this.koordinater = koordinater;
    }

    @Override
    public String toString(){
        return "Lat: " + koordinater[0] + " Long: " + koordinater[1];
    }
}
