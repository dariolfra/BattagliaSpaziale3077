package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class customDialog {
    public static void showDialog(Game g){
        Dialog dialog = new Dialog(g);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        Button annulla = (Button) dialog.findViewById(R.id.annulla);
        annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button abbandona = (Button) dialog.findViewById(R.id.abbandona);
        abbandona.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                g.Abbandona();
            }
        });

        dialog.show();
    }
}