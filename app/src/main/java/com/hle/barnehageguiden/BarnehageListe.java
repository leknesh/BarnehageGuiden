package com.hle.barnehageguiden;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class BarnehageListe extends AppCompatActivity
        implements Response.Listener<String>, Response.ErrorListener, View.OnClickListener {

    //endpoint for barnehage-api
    private static final String ENDPOINT = "https://www.barnehagefakta.no/api/Location/";

    private RecyclerView recyclerView;
    private ArrayList<Barnehage> barnehageArrayList = new ArrayList<>();
    private BarnehageAdapter barnehageAdapter;

    private String kommuneNr;
    private Location lokasjon;

    //logtag
    private static final String TAG = "JsonLog";
    private static int handling;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barnehage_liste);
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

        //henter kommuneNr fra intent
        Intent intent = getIntent();
        if (intent != null) {
            handling = intent.getIntExtra("handling", 0);

            if (handling == MainActivity.INTENT_KOMMUNE) {
                kommuneNr = intent.getStringExtra("kommuneNr");
                Log.d(TAG, "KommuneNr fra intent: " + kommuneNr);
            } else if (handling == MainActivity.INTENT_LOKASJON) {
                //? eller?
                lokasjon = intent.getParcelableExtra("lokasjon");
                Log.d(TAG, "Koordinater fra intent: " + lokasjon.toString());
            }
        }

        recyclerView = findViewById(R.id.barnehage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        barnehageAdapter = new BarnehageAdapter(this, barnehageArrayList);
        recyclerView.setAdapter(barnehageAdapter);

        hentBarnehageData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.action_mainActivity:
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                // Gjøre noe her?!
        } */
        return super.onOptionsItemSelected(item);

    }

    private void hentBarnehageData() {
        String URL = ENDPOINT;

        if (handling == MainActivity.INTENT_KOMMUNE){
            URL += "kommune/" + kommuneNr;
        }
        else if (handling == MainActivity.INTENT_LOKASJON){
            URL += lokasjon.getLatitude() + "/" + lokasjon.getLongitude() + "/0.01";
        }

        if (isOnline()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, this, this);
            queue.add(stringRequest);
        } else {
            //toastMsg("Ingen nettverkstilgang. Kan ikke laste varer.");
        }
    }

    @Override
    public void onResponse(String response) {

        genererListe(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    //oppretter arraylist fra volleyrespons
    private void genererListe(String response) {

        barnehageArrayList.clear();

        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {

                Barnehage bhg = new Barnehage(jsonArray.getJSONObject(i));
                Log.d(TAG, bhg.toString());
                barnehageArrayList.add(bhg);
            }
            updateListView(barnehageArrayList);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateListView(ArrayList<Barnehage> barnehageArrayList) {

        barnehageAdapter = new BarnehageAdapter(this, barnehageArrayList);
        recyclerView.setAdapter(barnehageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    // Checks network connection
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    //skipper fragmentet
    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

    }




}
