package com.hle.barnehageguiden;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, Response.ErrorListener, Response.Listener<String>,
        KommuneFragment.OnFragmentInteractionListener {

    //Layoutelementer
    private Spinner fylkeSpinner;
    private Spinner kommuneSpinner;

    //spinnerdata
    private ArrayList<Fylke> fylkeListe = new ArrayList<>();
    private ArrayList<String> fylkeNavnListe = new ArrayList<>();
    private ArrayList<Kommune> kommuneListe = new ArrayList<>();
    private ArrayList<String> kommuneNavnListe = new ArrayList<>();

    //endpoint for CRUD-api
    private static final String ENDPOINT = "https://data-nbr.udir.no/";
    private static final String FYLKE_URL = ENDPOINT + "fylker";
    private static final String KOMMUNE_URL = ENDPOINT + "kommuner/";
    private static final String KOMMUNE_INFO_URL = "https://www.barnehagefakta.no/api/Kommune/";

    public static final int INTENT_KOMMUNE = 1;
    public static final int INTENT_LOKASJON = 2;
    public static final int REQUEST_LOCATION_PERMISSION = 3;

    //Shared Preferences
    private SharedPreferences sharedPref;

    //logtag
    private static final String TAG = "JsonLog";

    public static final String PREF_USER_CREDENTIALS = "com.hle.barnehageguiden";

    String fylkeNavn, fylkeNr, kommuneNavn, kommuneNr;
    private int lagretFylkeIndex, lagretKommuneIndex;
    private int valgtFylkeIndex, valgtKommuneIndex;
    public KommuneInfo kommuneInfo;

    //lokasjonsvariabler. Hentet fra Codelabs
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallBack;
    private Location lokasjon;
    private boolean mTrackingLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initierer GUI-elementer
        Button kommuneKnapp = findViewById(R.id.vis_kommune_knapp);
        kommuneKnapp.setOnClickListener(this);
        Button visKartKnapp = findViewById(R.id.vis_kart_knapp);
        visKartKnapp.setOnClickListener(this);
        fylkeSpinner = findViewById(R.id.fylke_spinner);
        kommuneSpinner = findViewById(R.id.kommune_spinner);

        // Oppdaterer og henter settingsverdier
        sharedPref = androidx.preference.PreferenceManager.
                getDefaultSharedPreferences(this);

        // Settings får "default"-verdier
        androidx.preference.PreferenceManager.
                setDefaultValues(this, R.xml.root_preferences, false);

        sjekkPreferences(sharedPref);
        Log.d(TAG, "Lagret fylkeNavn: " + lagretFylkeIndex + ", kommuneNavn: " + lagretKommuneIndex);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        hentFylkeData();
    }

    @Override
    public void onPause(){
        super.onPause();
        stopTrackingLocation();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("fylkeIndex", valgtFylkeIndex);
        savedInstanceState.putInt("kommuneIndex", valgtKommuneIndex);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null){
            valgtFylkeIndex = savedInstanceState.getInt("fylkeIndex", 0);
            valgtKommuneIndex = savedInstanceState.getInt("kommuneIndex", 0);
        }

    }

    /*
    //Foreløpig ikke i bruk
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        }); */

    // Lagrer brukernavn og passord til lokal fil på enhet.
    private void lagreSted() {
        SharedPreferences myPreferences = getSharedPreferences(PREF_USER_CREDENTIALS, MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();

        String key = ("lagret_fylke");
        int value = fylkeSpinner.getSelectedItemPosition();
        Log.d(TAG, "Lagrested, Fylke lagret: " + value);
        editor.putInt(key, value);

        key = "lagret_kommune";
        value = kommuneSpinner.getSelectedItemPosition();
        editor.putInt(key, value);

        Log.d(TAG, "Lagrested, kommuneNavn lagret: " + value);

        editor.apply();
    }

    // Sjekker settings for lagring av sted samt alder
    private void sjekkPreferences(SharedPreferences sharedPref) {
        boolean lagreKommune = sharedPref.
                getBoolean(SettingsActivity.KEY_PREF_SAVE_SWITCH, false);
        Log.d(TAG, "Lagre kommune? " + lagreKommune);

        int lagretAlder = sharedPref.
                getInt(SettingsActivity.KEY_PREF_AGE_ENTRY, 0);

        // Hvis lagring er valgt hentes lagrede verdier
        if (lagreKommune) {
            hentLagretSted();
            Log.d(TAG, "Lagring valgt");
        }
    }

    // Henter og setter spinnere til lagret valg
    private void hentLagretSted() {
        SharedPreferences myPreferences = getSharedPreferences(MainActivity.PREF_USER_CREDENTIALS,
                MODE_PRIVATE);
        String fylke_key = "lagret_fylke";
        int fylke_value = myPreferences.getInt(fylke_key, 0);
        if (fylke_value != 0) {
            lagretFylkeIndex = fylke_value;
            Log.d(TAG, "FylkeValue: " + fylke_value);
        }

        String kommune_key = "lagret_kommune";
        int kommune_value = myPreferences.getInt(kommune_key, 0);
        if (kommune_value != 0) {
            lagretKommuneIndex = kommune_value;
            Log.d(TAG, "KommuneValue: " + kommune_value);
        }

    }


    /********************************************************
     * Starter Volley-request for fylkesliste
     */
    private void hentFylkeData() {

        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, FYLKE_URL, this, this);
            queue.add(stringRequest);
        }
        else {
            displayToast("Ingen nettverkstilgang. Kan ikke laste listen.");
        }
    }

    /********************************************************
     * Bygger fylkesliste fra Volley-response
     */
    private void byggFylkeListe(String response) {
        JSONArray jsonArray = null;

        fylkeListe.clear();
        fylkeNavnListe.clear();

        fylkeNavnListe.add(0, "Velg fylkeNavn");

        try {
            jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {

                Fylke f = new Fylke(jsonArray.getJSONObject(i));
                Log.d(TAG, f.toString());
                fylkeListe.add(f);
                fylkeNavnListe.add(f.fylkeNavn);
                byggFylkeSpinner();
            }

        } catch (JSONException e) {
                e.printStackTrace();
        }
    }


    /********************************************************
     * Bygger spinner med listener. Data generert i byggFylkeListe
     */
    private void byggFylkeSpinner() {

        fylkeNr = "";

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fylkeNavnListe);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (fylkeSpinner != null) {
            fylkeSpinner.setAdapter(spinnerAdapter);

            if (valgtFylkeIndex != 0 && valgtFylkeIndex < fylkeNavnListe.size()){
                fylkeSpinner.setSelection(valgtFylkeIndex);
            }
            //setter lagret fylke, sjekker for å unngå exception. +1 for infotext i listen.
            else if (lagretFylkeIndex < fylkeNavnListe.size()) {
                fylkeSpinner.setSelection(lagretFylkeIndex);
            }

        }

        //legger på lytter

        fylkeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                     fylkeNavn = adapterView.getItemAtPosition(pos).toString();
                     //settes for evt lagring i savedInstanceState
                     valgtFylkeIndex = pos;

                     //nullstiller kommunelisten for evt bytte av fylke
                     kommuneNavnListe.clear();
                     kommuneListe.clear();

                    //henter fylkesnr
                    for (Fylke f : fylkeListe){
                        if (f.fylkeNavn == fylkeNavn)
                            fylkeNr = f.fylkeNr;
                    }

                    if (!fylkeNr.isEmpty())
                        hentKommuneListeData(fylkeNr);

                    Log.d(TAG, fylkeNavn);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    /********************************************************
     * Initierer VolleyRrequest for liste over kommuner i det valgte fylket
     */
    private void hentKommuneListeData(String fylkeNr) {
        String URL;

        //formatterer URL til spørring:
        if (fylkeNr.length() == 1)
            URL = KOMMUNE_URL + "0" + fylkeNr;
        else
            URL = KOMMUNE_URL + fylkeNr;

        //henter kommuneliste til spinneren (kommer ut i onResponse)
        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, this, this);
            queue.add(stringRequest);
            Log.d(TAG, KOMMUNE_URL + fylkeNr  + " " + URL);
        }
        else {
            displayToast("Ingen nettverkstilgang. Kan ikke laste listen.");
        }
    }

    /********************************************************
     * Bearbeider VolleyResponse, legger i arraylister
     */
    private void byggKommuneListe(String response) {
        kommuneListe.clear();
        kommuneNavnListe.clear();

        //legger på forklaring hvis listen er tom
         kommuneNavnListe.add(0, "Velg kommuneNavn");

        try {
            Log.d(TAG, response);

            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                Kommune k = new Kommune(jsonArray.getJSONObject(i));
                Log.d(TAG, k.toString());
                kommuneListe.add(k);
                kommuneNavnListe.add(k.kommuneNavn);

                byggKommuneSpinner();
            }
        } catch (JSONException ex) {
            Log.d(TAG, "JSONException");
        }
    }

    /********************************************************
     * Henter kommuneliste fra API og bygger spinner med listener.
     */
    private void byggKommuneSpinner() {

        //knytter kommuneliste til spinneren
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kommuneNavnListe);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (kommuneSpinner != null)
            kommuneSpinner.setAdapter(spinnerAdapter);

        Log.d(TAG, "Indekser: VK = " + valgtKommuneIndex + " VF = " + valgtFylkeIndex);

        //sjekker om savedInstanceState kommune finnes og om fylkene stemmer overens
        if (valgtKommuneIndex != 0 && valgtKommuneIndex < kommuneNavnListe.size()
                && valgtFylkeIndex == fylkeSpinner.getSelectedItemPosition()) {

            kommuneSpinner.setSelection(valgtKommuneIndex);

        }
        //setter lagret kommune, sjekker for å unngå exception. +1 for infotext i listen.
        //setter ikke lagret kommune dersom fylkesvalg er endret fra lagret verdi.
        else if (lagretKommuneIndex < kommuneNavnListe.size() && fylkeSpinner.getSelectedItemPosition() == lagretFylkeIndex) {

            kommuneSpinner.setSelection(lagretKommuneIndex);

        }

        //legger på lytter
        kommuneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                kommuneNavn = adapterView.getItemAtPosition(pos).toString();
                valgtKommuneIndex = pos;
                Log.d(TAG, kommuneNavn);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //behandler menyvalg
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vis_kommune_knapp:
                lagreSted();

                for (Kommune k : kommuneListe){
                    if (k.kommuneNavn == kommuneNavn)
                        kommuneNr = k.kommuneNr;
                }
                Log.d(TAG, "KommuneNr til fragment: " + kommuneNr);
                hentKommuneInfo(kommuneNr);
                break;
            case R.id.vis_kart_knapp:

                hentLokasjonsTillatelse();
                //startLocationIntent();

                break;
            default:
                //ingenting
                break;

        }
    }

    //hentet fra codelabs
    private void hentLokasjonsTillatelse() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "Lokasjonstillatelser OK");
            startTrackingLocation();
        }
    }

    //hentet fra codelabs
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "LocationPermission OK");
                    startTrackingLocation();
                } else {
                    displayToast("Mangler tilgang til lokasjonsdata");
                }
                break;
            default:
                break;
        }
    }

    private void startTrackingLocation() {
        Log.d(TAG, "StartTrackingLocation");
        mLocationCallBack = createLocationCallback();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "StartTracking & PermissionFALSE");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);

        } else {
            Log.d(TAG, "StartTracking & PermissionTRUE");
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), createLocationCallback(), null);
            mTrackingLocation = true;

        }

    }

    private void stopTrackingLocation(){
        if (mTrackingLocation){
            mFusedLocationClient.removeLocationUpdates(mLocationCallBack);
            mTrackingLocation = false;
            Log.d(TAG, "StopTrackingLocation");
        }
    }

    private LocationRequest getLocationRequest() {
        Log.d(TAG, "getLocationRequest");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG, "LocationRequest ferdig");
        return locationRequest;
    }

   private LocationCallback createLocationCallback() {
       Log.d(TAG, "createLocationCallback");
       //usikker på hvor denne skal være?
       mLocationCallBack = new LocationCallback() {
           @Override
           public void onLocationResult(LocationResult locationResult) {
               Log.d(TAG, "OnLocationResult");
               if (mTrackingLocation) {
                   Log.d(TAG, "mTrackingLocation");
                   startLocationIntent(locationResult.getLastLocation());
               }
           }
       };

       return mLocationCallBack;
   }

    private void startLocationIntent(Location location) {
        Log.d(TAG, "StartLocationIntent");
        //start aktivitet Barnehageliste med location som extra

        Intent intent = new Intent(this, BarnehageListe.class);
        intent.putExtra("lokasjon", location);
        intent.putExtra("handling", INTENT_LOKASJON);
        startActivity(intent);

    }

    private void hentKommuneInfo(String kommuneNr){
        String URL = KOMMUNE_INFO_URL + kommuneNr;

        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, this, this);
            queue.add(stringRequest);
            Log.d(TAG, URL);

        }

    }

    private void lagKommuneInfoObjekt(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jo = jsonObject.getJSONObject("indikatorDataKommune");
            Log.d(TAG, jo.toString());
            kommuneInfo = new KommuneInfo(jo);
            kommuneInfo.setKommuneInfoNavn(kommuneNavn);
            kommuneInfo.setKommuneInfoNr(kommuneNr);
            Log.d(TAG, "" + kommuneInfo.kommuneInfoNr);

            //kaller fragmentvisning
            visKommuneFragment(kommuneInfo);
            Log.d(TAG, "Jeppsipeppsi");

        } catch (JSONException ex){
            Log.d(TAG, "JSONException");
        }
    }
    //ønsker ikke fragment i backstack
    private void visKommuneFragment(KommuneInfo kommuneInfo) {

        KommuneFragment kommuneFragment = KommuneFragment.newInstance(fylkeNavn, kommuneInfo);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, kommuneFragment).addToBackStack(null).
                commit();
    }



    // Checks network connection
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    //viser toastmelding med valgt tekstinput
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }


    //svar på VolleyRequest for fylke, kommune eller kommunedetaljer. Skilles fra hverandre ved sjekk på info som kun finnes i fylkeslisten.
    @Override
    public void onResponse(String response) {
        Log.d(TAG, response);
        try {
            //hvis svaret er fylkeslisten
            if (response.contains("OrgNrFylkesmann")) {
                byggFylkeListe(response);

            //hvis svaret er kommunedetaljer
            } else if (response.contains("indikatorDataKommune")) {
                Log.d(TAG, "hei");

                lagKommuneInfoObjekt(response);

            //svaret er kommuneliste
            } else {
               byggKommuneListe(response);
            }

        } catch (Exception ex) {
            Log.d(TAG, "ResponseException");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d(TAG, "VolleyError: " + error.toString());
    }

    //ønsker ikke å gå tilbake til fragment
    @Override
    public void onFragmentInteraction(Uri uri) {
        getSupportFragmentManager().popBackStackImmediate();
    }
}




