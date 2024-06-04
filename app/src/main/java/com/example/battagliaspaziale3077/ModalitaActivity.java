package com.example.battagliaspaziale3077;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class ModalitaActivity extends AppCompatActivity {
    ImageButton btn_User_vs_AI, btn_User_vs_User_connect, btn_User_vs_User_hosted;
    Animation scale_down, scale_up;
    Button btn_regole;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selezione_modalita);

        btn_User_vs_AI = findViewById(R.id.btn_user_vs_ai);
        btn_User_vs_User_connect = findViewById(R.id.btn_mod_user_vs_user_conn);
        btn_User_vs_User_hosted = findViewById(R.id.bnt_mod_user_vs_user_host);
        btn_regole = findViewById(R.id.btn_regole);

        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        btn_User_vs_AI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_User_vs_AI.startAnimation(scale_down);
                btn_User_vs_AI.startAnimation(scale_up);
                Intent user_vs_ai = new Intent(ModalitaActivity.this, User_vs_Ai_Activity.class);
                startActivity(user_vs_ai);
            }
        });

        btn_User_vs_User_connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                btn_User_vs_User_connect.startAnimation(scale_down);
                btn_User_vs_User_connect.startAnimation(scale_up);
                Intent user_vs_user_conn = new Intent(ModalitaActivity.this, User_vs_User_connect_Activity.class);
                startActivity(user_vs_user_conn);
            }
        });

        btn_User_vs_User_hosted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_User_vs_User_hosted.startAnimation(scale_down);
                btn_User_vs_User_hosted.startAnimation(scale_up);
                Intent user_vs_user_host = new Intent(ModalitaActivity.this, User_vs_User_host_Activity.class);
                startActivity(user_vs_user_host);
            }
        });

    }
    public void btn_regole_pressed(View v){
        btn_regole.startAnimation(scale_down);
        btn_regole.startAnimation(scale_up);
        regoleDialogNoGame.showDialog(this);
    }
}
