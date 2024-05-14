package com.example.battagliaspaziale3077;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ModalitaActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selezione_modalita);

        ImageButton btn_User_vs_AI = findViewById(R.id.btn_user_vs_ai);
        ImageButton btn_User_vs_User_connect = findViewById(R.id.btn_mod_user_vs_user_conn);
        ImageButton btn_User_vs_User_hosted = findViewById(R.id.bnt_mod_user_vs_user_host);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        btn_User_vs_AI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user_vs_ai = new Intent(ModalitaActivity.this, User_vs_Ai_Activity.class);
                startActivity(user_vs_ai);
            }
        });

        btn_User_vs_User_connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent user_vs_user_conn = new Intent(ModalitaActivity.this, User_vs_User_connect_Activity.class);
                startActivity(user_vs_user_conn);
            }
        });

        btn_User_vs_User_hosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user_vs_user_host = new Intent(ModalitaActivity.this, User_vs_User_host_Activity.class);
                startActivity(user_vs_user_host);
            }
        });


    }
}
