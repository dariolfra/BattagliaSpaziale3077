package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class User_vs_Ai_Activity extends AppCompatActivity {

    Button btn_gioca, btn_regole;
    TextInputEditText txt_nome;
    String nome_giocatore;
    Boolean nome_corretto;
    int modalita = 1;
    Animation scale_down, scale_up;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_ai);

        Window window = this.getWindow();
        Context context = this.getApplicationContext();
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

        btn_gioca = (Button) findViewById(R.id.btn_gioca);
        btn_regole = (Button) findViewById(R.id.btn_regole);
        txt_nome = (TextInputEditText) findViewById(R.id.txt_nome);

        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        btn_gioca.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                btn_gioca.startAnimation(scale_down);
                btn_gioca.startAnimation(scale_up);
                nome_corretto = false;
                try {
                    nome_giocatore = String.valueOf(txt_nome.getText());
                    if (nome_giocatore.isEmpty()) {
                        throw new Exception();
                    } else {
                        nome_corretto = true;
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                    CustomToast.showToast(context, "INSERISCI NOME GIOCATORE", Toast.LENGTH_SHORT);
                    nome_corretto = false;
                }
                if (nome_corretto) {
                    txt_nome.setText("");
                    Intent gioco = new Intent(User_vs_Ai_Activity.this, PersonaggiActivity.class);
                    gioco.putExtra("mod", modalita);
                    gioco.putExtra("nome", nome_giocatore);
                    startActivity(gioco);
                }
            }
        });
    }

    public void btn_regole_pressed(View v) {
        btn_regole.startAnimation(scale_down);
        btn_regole.startAnimation(scale_up);
        regoleDialogNoGame.showDialog(this);
    }
}