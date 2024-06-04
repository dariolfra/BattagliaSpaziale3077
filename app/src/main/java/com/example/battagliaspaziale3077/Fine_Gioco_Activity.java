package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.Serializable;

public class Fine_Gioco_Activity extends AppCompatActivity implements Serializable {
    TextView txt_nome, txt_risultato;
    Button btn_home;
    ImageView img_1, img_2;
    Animation scale_down, scale_up;
    String nome_giocatore = "";
    boolean risulato = false;
    int id_personaggio;
    MediaPlayer mp;
    Context context;
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fine_gioco);

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

        context = this.getApplicationContext();

        txt_nome = findViewById(R.id.txt_Nome);
        txt_risultato = findViewById(R.id.txt_risultato);
        btn_home = findViewById(R.id.btn_home);
        img_1 = findViewById(R.id.img_1);
        img_2 = findViewById(R.id.img_2);

        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        Intent gioco = getIntent();
        nome_giocatore = gioco.getStringExtra("nome");
        risulato = gioco.getBooleanExtra("risultato", false);
        id_personaggio = gioco.getIntExtra("id_personaggio", 1);

        suono_personaggio(id_personaggio);

        txt_nome.setText(nome_giocatore);
        if(risulato){
            txt_risultato.setText("HA VINTO");
            img_1.setImageResource(R.drawable.coppa);
            img_2.setImageResource(R.drawable.coppa);
        }
        else{
            txt_risultato.setText("HA PERSO");
            img_1.setImageResource(R.drawable.pianto);
            img_2.setImageResource(R.drawable.pianto);
        }
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_home.startAnimation(scale_down);
                btn_home.startAnimation(scale_up);
                Intent home = new Intent(Fine_Gioco_Activity.this, HomeActivity.class);
                startActivity(home);
            }
        });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        indietroDialog.showDialog(this);
    }

    public void suono_personaggio(int indice){
        if(indice == 1){
            mp = MediaPlayer.create(context, R.raw.blur);
            mp.start();
        }
        else if (indice == 2){
            mp = MediaPlayer.create(context, R.raw.meloni);
            mp.start();
        }
        else if(indice == 3){
            mp = MediaPlayer.create(context, R.raw.brasiliano);
            mp.start();
        }
        else if(indice == 4){
            mp = MediaPlayer.create(context, R.raw.ciccio);
            mp.start();
        }
        else if(indice == 5){
            mp = MediaPlayer.create(context, R.raw.marza);
            mp.start();
        }
        else if(indice == 6){
            mp = MediaPlayer.create(context, R.raw.optimusprime);
            mp.start();
        }
        else if(indice == 7){
            mp = MediaPlayer.create(context, R.raw.papa);
            mp.start();
        }
        else if(indice == 8){
            mp = MediaPlayer.create(context, R.raw.pefforza);
            mp.start();
        }
        else if(indice == 9) {
            mp = MediaPlayer.create(context, R.raw.shiva);
            mp.start();
        }
        else if(indice == 10){
            mp = MediaPlayer.create(context, R.raw.kungfupanda);
            mp.start();
        }

    }
}
