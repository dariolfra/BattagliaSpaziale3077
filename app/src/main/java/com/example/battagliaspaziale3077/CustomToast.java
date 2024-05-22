package com.example.battagliaspaziale3077;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    public static void showToast(Context context, String message, int duration){
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        TextView textView = layout.findViewById(R.id.toastMessage);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 120);
        toast.setView(layout);
        toast.show();
    }
}
