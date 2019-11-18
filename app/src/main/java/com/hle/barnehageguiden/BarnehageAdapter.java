package com.hle.barnehageguiden;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BarnehageAdapter extends RecyclerView.Adapter<BarnehageAdapter.BarnehageViewHolder> {

    //logtag
    private static final String TAG = "JsonLog";

    private ArrayList<Barnehage> barnehageListe = new ArrayList<>();
    private Context mContext;

    BarnehageAdapter(Context context, ArrayList<Barnehage> barnehageListe) {
        this.barnehageListe = barnehageListe;
        this.mContext = context;
    }

    @Override
    public BarnehageAdapter.BarnehageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BarnehageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.barnehage_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BarnehageAdapter.BarnehageViewHolder holder, int position) {
        Barnehage denneBarnehagen = barnehageListe.get(position);
        holder.bindTo(denneBarnehagen);
    }

    @Override
    public int getItemCount() {
        return barnehageListe.size();
    }

    class BarnehageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String IMG_ENDPONT = "https://itfag.usn.no/~216561/img/";

        private TextView navnTextView;
        private TextView antallTextView;
        private TextView eierformTextView;

        BarnehageViewHolder(View itemView) {
            super(itemView);

            navnTextView = itemView.findViewById(R.id.bhg_Navn_Card);
            antallTextView = itemView.findViewById(R.id.bhg_AntallBarn_Card);
            eierformTextView = itemView.findViewById(R.id.bhg_Eierform_Card);

            itemView.setOnClickListener(this);
        }

        void bindTo(Barnehage denneBarnehagen) {
            navnTextView.setText(denneBarnehagen.getBarnehageNavn());
            antallTextView.setText("Antall Barn: " + denneBarnehagen.getAntallBarn());
            eierformTextView.setText(denneBarnehagen.getEierform());

        }

        @Override
        public void onClick(View v) {
            Barnehage denneBarnehagen = barnehageListe.get(getAdapterPosition());

            Intent intent = new Intent(mContext, BarnehageDetaljActivity.class);

            //sender med barnehageId for oppslag)
            intent.putExtra("barnehageNr", denneBarnehagen.getBarnehageNr());
            Log.d(TAG, "Valgt barnehage Nr: " + denneBarnehagen.getBarnehageNr());

            mContext.startActivity(intent);
        }
    }

}
