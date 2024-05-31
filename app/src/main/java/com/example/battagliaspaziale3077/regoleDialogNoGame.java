package com.example.battagliaspaziale3077;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
public class regoleDialogNoGame {
    static Animation scale_up, scale_down;

    public static void showDialog(Context context){
        Dialog dialog = new Dialog(context);
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

        scale_down = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(context, R.anim.scale_up);

        btn_annulla.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                btn_annulla.startAnimation(scale_down);
                btn_annulla.startAnimation(scale_up);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
