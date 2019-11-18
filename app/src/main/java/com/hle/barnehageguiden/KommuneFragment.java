package com.hle.barnehageguiden;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KommuneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KommuneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KommuneFragment extends Fragment implements View.OnClickListener {

    private static final String FYLKE_INPUT = "fylkeInput";
    private static final String KOMMUNE_INPUT = "kommuneInput";
    public String fylke;
    public KommuneInfo kommuneInfo;
    public TextView kommuneView, barnBhgAntallView, barnPrAnsattView, andelUtdannetView,
            lekearealView, pedagognormView;
    public Button visBhgButton;

    private OnFragmentInteractionListener mListener;

    public KommuneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment KommuneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KommuneFragment newInstance(String fylke, KommuneInfo kommuneInfo) {
        KommuneFragment fragment = new KommuneFragment();
        Bundle args = new Bundle();
        args.putString(FYLKE_INPUT, fylke);
        args.putSerializable(KOMMUNE_INPUT, kommuneInfo);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fylke = getArguments().getString(FYLKE_INPUT);
            kommuneInfo = (KommuneInfo) getArguments().getSerializable(KOMMUNE_INPUT);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.fragment_kommune, container, false);

        kommuneView = fragmentView.findViewById(R.id.fragment_kommune);
        kommuneView.setText(kommuneInfo.kommuneInfoNavn + " i " + fylke );

        barnBhgAntallView = fragmentView.findViewById(R.id.barn_bhg_antall);
        barnBhgAntallView.setText("" + kommuneInfo.getAntallBarn());

        barnPrAnsattView = fragmentView.findViewById(R.id.barn_ansatt_antall);
        barnPrAnsattView.setText("" + kommuneInfo.getBarnPrAnsatt());

        andelUtdannetView = fragmentView.findViewById(R.id.andel_utdannet);
        andelUtdannetView.setText("" + kommuneInfo.getAndelUtdannet());

        lekearealView = fragmentView.findViewById(R.id.lekeareal);
        lekearealView.setText("" + kommuneInfo.getArealPrBarn());

        pedagognormView = fragmentView.findViewById(R.id.andel_pedagognorm);
        pedagognormView.setText("" + kommuneInfo.getAndelNormOk());

        visBhgButton = fragmentView.findViewById(R.id.vis_bhg_knapp);
        visBhgButton.setOnClickListener(this);

        return fragmentView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {

        super.onPause();

    }

    //Knapp starter neste activity med input kommuneNr
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), BarnehageListe.class);
        intent.putExtra("kommuneNr", kommuneInfo.getKommuneInfoNr());
        intent.putExtra("handling", MainActivity.INTENT_KOMMUNE);
        startActivity(intent);
    }

    //Ikke i bruk
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
