package com.hle.barnehageguiden;

//API RequestURL: https://data-nbr.udir.no/fylker

import android.database.DatabaseUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Fylke {

    public String fylkeNavn;
    public String fylkeNr;

    private static final String KOL_FYLKENAVN       = "Navn";
    private static final String KOL_FYLKENR         = "Fylkesnr";

    //stringkonstruktør
    public Fylke (String fylkeNavn, String fylkeNr){
        this.fylkeNavn = fylkeNavn;
        this.fylkeNr = fylkeNr;
    }

    //JSON-konstruktør
    public Fylke (JSONObject jsonFylke){
        this.fylkeNr    = jsonFylke.optString(KOL_FYLKENR);
        this.fylkeNavn  = jsonFylke.optString(KOL_FYLKENAVN);
    }

    public String getFylkeNavn() {
        return fylkeNavn;
    }

    public String getFylkeNr() {
        return fylkeNr;
    }

    public static ArrayList<Fylke> lagFylkeListe(JSONObject jsonObjectListe) throws JSONException, NullPointerException {
        ArrayList<Fylke> fylkeListe = new ArrayList<>();

        for (int i=0; i<jsonObjectListe.length(); i++){
            JSONObject jsonFylke = jsonObjectListe.getJSONObject(null);
            Fylke detteFylket = new Fylke(jsonFylke);
            fylkeListe.add(detteFylket);
            Log.d("JsonLog", "Stopper i lagFylkeListe");
        }

        return fylkeListe;
    }
    
    public static ArrayList<String> lagFylkeNavnListe (ArrayList<Fylke> fylkeListe){
        ArrayList<String> fylkeNavnListe = new ArrayList<>();
        for ( Fylke fylke : fylkeListe) {
            fylkeNavnListe.add(fylke.fylkeNavn);
            Log.d("JsonLog", "Stopper i lagFylkeNavnListe");
        }
        return fylkeNavnListe;
    }

    @Override
    public String toString(){

        return fylkeNavn;
    }


}
