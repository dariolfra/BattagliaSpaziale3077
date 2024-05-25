package com.example.battagliaspaziale3077;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {
    Context context;
    int[] immaginiCasella;


    LayoutInflater Inflater;

    public GridAdapter(Context context, int[] immaginiCasella) {
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

    public boolean ControllaSeLiberi(int position, int size,int index, int rotation) {
        if (position < 0 || position >= immaginiCasella.length){
            return false;
        }
        //Per risolvere bug
        boolean celleLibere = true;
        if(index == 0 && (rotation == 0 || rotation == 180 || rotation == 90) ||
                index == 1 && (rotation == 0 || rotation == 180) ||
                index == 2 && (rotation == 0 || rotation == 90) ||
                index == 3 && (rotation == 0 || rotation == 180) ||
                index == 4 && rotation == 90 ||
                index == 5 && rotation == 90) {
            int[] invalidPositions = {9, 19, 29, 39, 49, 59, 69, 79, 89, 99};
            for (int invalidPosition : invalidPositions) {
                if (position == invalidPosition) {
                    return false;
                }
            }
        }

        //Verifica se le celle dove la nave deve essere inserita sono libere
        for (int i = 0; i < size; i++) {
            if ((index == 0 || index == 1 || index == 2 || index == 3 || index == 4 || index == 5) && rotation == 0 || (index == 1 || index == 3) && rotation == 180 || index == 2 && rotation == 270)  {
                if (CellaOccupata(position + i, immaginiCasella)) {
                    return false;
                }
                if (index == 0 && i == 1 || index == 2 && i == 0 && rotation == 0 || index == 2 && i == 1 && rotation == 270 || index == 4 && i == 2 || index == 5 && i == 2) {
                    if (CellaOccupata(position + i - 10, immaginiCasella)) {
                        return false;
                    }
                }
            } else if ((index == 0 || index == 1 || index == 3 || index == 4 || index == 5) && rotation == 90 || (index == 1 || index == 3) && rotation == 270) {
                if (CellaOccupata(position + i * 10, immaginiCasella)) {
                    return false;
                }
                if (index == 0 && i == 1 || index == 4 && i == 2 || index == 5 && i == 2) {
                    if (CellaOccupata(position + 1 + i * 10, immaginiCasella)) {
                        return false;
                    }
                }
            } else if ((index == 0 || index == 4 || index == 5) && rotation == 180 || index == 2 && (rotation == 90 || rotation == 180)) {
                if (CellaOccupata(position + i, immaginiCasella)) {
                    return false;
                }
                if (index == 0 && i == 1 || index == 2 && i == 0 && rotation == 90 || index == 2 && i == 1 && rotation == 180 || index == 4 && i == 2 || index == 5 && i == 0) {
                    if (CellaOccupata(position + i + 10, immaginiCasella)) {
                        return false;
                    }
                }
            } else if ((index == 0 || index == 4 || index == 5) && rotation == 270) {
                if (CellaOccupata(position + i * 10, immaginiCasella)) {
                    return false;
                }
                if (index == 0 && i == 1 || index == 4 && i == 2 || index == 5 && i == 0) {
                    if (CellaOccupata(position - 1 + i * 10, immaginiCasella)) {
                        return false;
                    }
                }

            }
        }
        return celleLibere;
    }
    public int AggiustaPosizioni(int index,int rotation,int position){
        //Aggiustamenti di posizioni altrimenti l'immagine non verrà posizionata dove l'utente ha messo la nave
        int posizione = position;
        if ((index == 0 || index == 1 || index == 2) && rotation == 90){
            if(position % 10 == 0){
                posizione = position - 10;
            }
            else {
                posizione = position - 9;
            }
        } else if (index == 3 && rotation == 90) {
            if(position % 10 == 0 || position % 9 == 0){
                posizione = position - 20;
            }
            else {
                posizione = position - 19;
            }
        } else if(index == 3 && (rotation == 0 || rotation == 180)){
            posizione = position - 1;
        } else if (index == 4 && rotation == 90) {
            posizione = position - 20 ;
        } else if (index == 5 && rotation == 90|| index == 5 && rotation == 180 && position < 99|| index == 0 && rotation == 180 && position < 99){ //altrimenti la nave la mette una riga sotto
            posizione = position - 10;
        } else if ((index == 5 || index == 0 ) && rotation == 270){
            posizione = position - 9;
        } else if (index == 4 && rotation == 180) {
            posizione = position - 11;
        } else if (index == 4 && rotation == 270) {
            posizione = position - 19;
        }
        return posizione;
    }
    public int getColumnFromPosition(int position) {
        return position % 10;
    } //colonne da 0 a 9
    public boolean CellaOccupata(int posizione,int[] immaginiCasella){
        if(immaginiCasella[posizione] != 0){
            return true; //se è occupata
        }
        else {
            return false;
        }
    }
}





