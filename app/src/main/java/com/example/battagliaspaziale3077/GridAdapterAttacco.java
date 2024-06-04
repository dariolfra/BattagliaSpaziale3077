package com.example.battagliaspaziale3077;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridAdapterAttacco extends BaseAdapter {
    Context context;
    int[] immaginiCasella;


    LayoutInflater Inflater;

    public GridAdapterAttacco(Context context, int[] immaginiCasella) {
        this.context = context;
        this.immaginiCasella = immaginiCasella;
    }

    @Override
    public int getCount() {
        return immaginiCasella.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (Inflater == null) {
            Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = Inflater.inflate(R.layout.grid_item, null);
        }

        ImageView imageView = convertView.findViewById(R.id.grid_image);

        imageView.setImageResource(immaginiCasella[position]);
        return convertView;
    }

    public int AggiustaPosizioni(int index, int rotation, int position) {
        //Aggiustamenti di posizioni per inserire la nave dove l'utente l'ha posizionata
        int posizione = position;
        if ((index == 3 || index == 7 || index == 6) && (rotation == 90 || rotation == 270)) {
            if (position % 10 == 0) {
                posizione = position - 10;
            } else {
                posizione = position - 19;
            }
        } else if (index == 9 && (rotation == 0 || rotation == 180)) {
            posizione = position - 9;
        } else if (index == 5 && rotation == 90) {
            if (position % 10 == 0 || position % 9 == 0) {
                posizione = position + 4;
            } else {
                posizione = position + 19; //sistemare
            }
        } else if (index == 1 && rotation == 0 || index == 7 && (rotation == 0 || rotation == 180)) {
            posizione = position - 1;

        } else if (index == 5 && (rotation == 0 || rotation == 180)) {
            posizione = position - 21;
        } else if (index == 1 && rotation == 90) {
            posizione = position - 20;
        } else if (index == 6 && rotation == 0) {
            posizione = position - 1;
        } else if (index == 6 && rotation == 180 && position < 99 || index == 3 && rotation == 180 && position < 99 ||
                index == 10 && rotation == 90 || index == 8) { //altrimenti la nave la mette una riga sotto
            posizione = position - 10;
        } else if (index == 1 && rotation == 180) {
            posizione = position - 11;
        } else if (index == 1 && rotation == 270) {
            posizione = position - 19;
        } else if (index == 10 && rotation == 270) {
            posizione = position + 10;
        } else if (index == 4 && (rotation == 0 || rotation == 180)) {
            posizione = position - 3;

        } else if (index == 4 && (rotation == 90 || rotation == 270)) {
            posizione = position - 39;
        }
        return posizione;
    }

    public int getColumnFromPosition(int position) {
        return position % 10;
    } //colonne da 0 a 9
}
