package com.example.battagliaspaziale3077;

import static com.example.battagliaspaziale3077.R.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), color.black));

        Button btn_gioca = findViewById(id.btn_Gioca);

        Button btn_vai_a_gioco = findViewById(id.bnt_vai_a_gioco);

        btn_gioca.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent selezione_modalita = new Intent(HomeActivity.this, ModalitaActivity.class);
                startActivity(selezione_modalita);
            }
        });

        btn_vai_a_gioco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gioco = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(gioco);
            }
        });
    }
}