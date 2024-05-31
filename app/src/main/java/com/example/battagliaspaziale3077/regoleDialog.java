package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
public class regoleDialog {

    public static void showDialog(Game g){
        Dialog dialog = new Dialog(g);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.regoledialog);

        ImageView img1 = (ImageView) dialog.findViewById(R.id.imageView);
        ImageView img2 = (ImageView) dialog.findViewById(R.id.imageView2);
        ImageView img3 = (ImageView) dialog.findViewById(R.id.imageView3);
        Button btn_annulla = (Button) dialog.findViewById(R.id.button);

        img1.setImageResource(R.drawable.selected);
        img2.setImageResource(R.drawable.nave_colpita);
        img3.setImageResource(R.drawable.nave_affondata);

        btn_annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
