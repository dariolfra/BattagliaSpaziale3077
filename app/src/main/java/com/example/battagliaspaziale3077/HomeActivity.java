package com.example.battagliaspaziale3077;

import static com.example.battagliaspaziale3077.R.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {
    Button btn_gioca, btn_vai_a_gioco;
    Context context;
    Animation scale_down, scale_up;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), color.black));

        context = this.getApplicationContext();

        btn_gioca = findViewById(id.btn_Gioca);

        btn_vai_a_gioco = findViewById(id.bnt_vai_a_gioco);

        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        btn_gioca.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btn_gioca.startAnimation(scale_down);
                btn_gioca.startAnimation(scale_up);
                Intent selezione_modalita = new Intent(HomeActivity.this, ModalitaActivity.class);
                startActivity(selezione_modalita);
            }
        });

        //elimina questo bottone
        btn_vai_a_gioco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gioco = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(gioco);
            }
        });
    }
}