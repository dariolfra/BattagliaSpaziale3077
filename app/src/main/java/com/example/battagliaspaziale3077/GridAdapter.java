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

    public boolean ControllaSeLiberi(int position, int size) {
        boolean celleLibere = true;
        for (int i = 0; i < size; i++){
            if(immaginiCasella[position + i] != 0){
                return false;
            }
        }
        return celleLibere;
    }
}


