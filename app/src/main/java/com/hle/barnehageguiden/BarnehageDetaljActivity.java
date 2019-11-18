package com.hle.barnehageguiden;

import android.app.Activity;
import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BarnehageDetaljActivity extends AppCompatActivity
        implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {

    private TextView bhgNavnView, adresseView, postNrView, postStedView, telefonNrView,
        mailadresseView, nettadresseView;
    private ImageButton ringButton, epostButton;

    private static final String BHG_ENDPOINT = "https://www.barnehagefakta.no/api/Barnehage/";

    private static final String TAB_KONTAKTINFO            = "kontaktinformasjon";
    private static final String TAB_ADRESSE                = "besoksAdresse";
    private static final String KOL_ADRESSE                = "adresselinje";
    private static final String KOL_POSTNR                 = "postnr";
    private static final String KOL_POSTSTED               = "poststed";
    private static final String KOL_TELEFONNR              = "telefon";
    private static final String KOL_MAILADRESSE            = "epost";
    private static final String KOL_NETTADRESSE            = "url";
    private static final String KOL_KOORDINATER            = "koordinatLatLng";

    private BarnehageDetalj barnehage = new BarnehageDetalj();

    private String bhgId;

    //logtag
    private static final String TAG = "JsonLog";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barnehage_detalj);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bhgId = intent.getStringExtra("barnehageNr");

        Log.d(TAG, "Intent-barnehage: " + bhgId);

        bhgNavnView = findViewById(R.id.barnehage_navn_view);
        adresseView = findViewById(R.id.bhg_gateadresse);
        postNrView = findViewById(R.id.bhg_postnummer);
        postStedView = findViewById(R.id.bhg_poststed);
        telefonNrView = findViewById(R.id.bhg_telefonNr);
        mailadresseView = findViewById(R.id.bhg_mailAdresse);
        nettadresseView = findViewById(R.id.bhg_nettside);
        nettadresseView.setOnClickListener(this);

        ringButton = findViewById(R.id.ring_button);
        ringButton.setOnClickListener(this);
        epostButton = findViewById(R.id.epost_button);
        epostButton.setOnClickListener(this);

        hentBarnehageData();
    }

    private void hentBarnehageData() {
        String URL = BHG_ENDPOINT + bhgId;

        Log.d(TAG, "URL til Volley: " + URL);

        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, this, this);
            queue.add(stringRequest);
        } else {
            Log.d(TAG, "Nettverksproblem!");
        }
    }

    @Override
    public void onResponse(String response) {

        Log.d(TAG, "Volleyresponse: " + response);
        byggBarnehage(response);

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    private void byggBarnehage(String response) {

        try {
            JSONObject heleObj = new JSONObject(response);


            JSONObject kontaktObj = heleObj.getJSONObject(TAB_KONTAKTINFO);
            JSONObject adresseObj = kontaktObj.getJSONObject(TAB_ADRESSE);
            JSONArray koordinatObj = heleObj.getJSONArray(KOL_KOORDINATER);

            barnehage = new BarnehageDetalj(heleObj);

            barnehage.setGateAdresse(adresseObj.getString(KOL_ADRESSE));
            barnehage.setPostNr(adresseObj.getString(KOL_POSTNR));
            barnehage.setPostSted(adresseObj.getString(KOL_POSTSTED));

            barnehage.setTelefonNr(kontaktObj.getString(KOL_TELEFONNR));
            barnehage.setMailAdresse(kontaktObj.getString(KOL_MAILADRESSE));
            barnehage.setNettAdresse(kontaktObj.getString(KOL_NETTADRESSE));

            double[] koordinatTab = new double[2];
            koordinatTab[0] = koordinatObj.getDouble(0);
            koordinatTab[1] = koordinatObj.getDouble(1);
            barnehage.setKoordinater(koordinatTab);

            Log.d(TAG, "Lat: " + barnehage.toString() );

            oppdaterView(barnehage);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONProblem!");
        }

    }

    private void oppdaterView(BarnehageDetalj barnehage) {

        //bhgNavnView.setText(barnehage.getBarnehageNavn());
        bhgNavnView.setText(barnehage.getBarnehageNavn());
        adresseView.setText(barnehage.getGateAdresse());
        postNrView.setText(barnehage.getPostNr());
        postStedView.setText(barnehage.getPostSted());
        telefonNrView.setText(barnehage.getTelefonNr());
        mailadresseView.setText(barnehage.getMailAdresse());
        nettadresseView.setText(barnehage.getNettAdresse());
    }


    // Checks network connection
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ring_button:
                ringBarnehagen();
                break;
            case R.id.epost_button:
                sendEmail();
                break;
            case R.id.bhg_nettside:
                visNettside();
                break;
        }
    }

    private void ringBarnehagen() {
        String telNr = barnehage.getTelefonNr().replaceAll("[^0-9]", "");
        telNr = "tel:" + telNr;
        Uri tlfUri = Uri.parse(telNr);
        Intent ringIntent = new Intent(Intent.ACTION_DIAL);
        ringIntent.setData(tlfUri);

        Log.d(TAG, "tlfNummer til intent: " + tlfUri);

        if (ringIntent.resolveActivity(getPackageManager()) != null)
            startActivity(ringIntent);
        else
            displayToast("Kan ikke ringe!");

    }
    //  NB! Kode hentet herfra:
    // https://stackoverflow.com/questions/8701634/send-email-intent
    private void sendEmail() {

        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setType("text/html");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, Uri.parse(barnehage.getMailAdresse()));
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Barnehageforespørsel");

        startActivity(Intent.createChooser(mailIntent, "Send epost"));

        /*
        btnEpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] epost = {denneBarnehagen.getEpost()};
                Log.d("log3", "Epost: " + epost);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, epost);
                if (emailIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(emailIntent);
            }
        });
         */
    }

    private void visNettside() {

        Uri webUri = Uri.parse(barnehage.getNettAdresse());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, webUri);

        if (browserIntent.resolveActivity(getPackageManager()) != null)
            startActivity(browserIntent);
        else
            displayToast("Kan ikke åpne nettleser!");
    }

    //viser toastmelding med valgt tekstinput
    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }




}
