package com.example.battagliaspaziale3077;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.cardview.widget.CardView;

public class indietroDialog {
    static Animation scale_down, scale_up;
    public static void showDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.indietrodialog);

        scale_down = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(context, R.anim.scale_up);

        Button btn_annulla = dialog.findViewById(R.id.btn_annulla);
        CardView card = dialog.findViewById(R.id.CardView);

        btn_annulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_annulla.startAnimation(scale_down);
                btn_annulla.startAnimation(scale_up);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
