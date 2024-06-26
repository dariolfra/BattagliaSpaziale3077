package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.Optional;

public class User_vs_User_connect_Activity extends AppCompatActivity implements Serializable {
    TextInputEditText txt_nome, txt_ip_server, txt_porta_server, lbl_codiceConn;
    String serverName, nome_giocatore1, nome_giocatore2;
    int serverPort;
    Button btn_connettiti, btn_regole;
    int modalita = 2;
    boolean connessione_instaurata;
    Context context;
    public boolean server_ha_scritto;
    Animation scale_down, scale_up;
    Socket client;
    String txtFromServer;
    private ClientThread comms;
    ConnectionFirebase connectionFirebase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_user_connect);

        btn_connettiti = (Button) findViewById(R.id.btn_connettiti);
        txt_nome = (TextInputEditText) findViewById(R.id.txt_input_nome);
        btn_regole = (Button) findViewById(R.id.btn_regole);


        //per connessione db
        connectionFirebase = new ConnectionFirebase();
        lbl_codiceConn = findViewById(R.id.lbl_codiceDiConn);

        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));
        context = this.getApplicationContext();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        btn_connettiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_connettiti.startAnimation(scale_down);
                btn_connettiti.startAnimation(scale_up);
                String codice = String.valueOf(lbl_codiceConn.getText());
                nome_giocatore2 = txt_nome.getText().toString();
                if (nome_giocatore2.isEmpty() || codice.isEmpty()) {
                    CustomToast.showToast(context, "COMPILA TUTTI I CAMPI!", Toast.LENGTH_SHORT);
                } else {
                    //metodo per unirmi alla partita
                    connectionFirebase.unisciAPartita(codice, nome_giocatore2, new ValueEventListener() {

                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Verifica se il nome del giocatore 1 è stato impostato
                            String nomeGiocatore1 = snapshot.child("nomeGiocatore1").getValue(String.class);
                            if (nomeGiocatore1 != null && !nomeGiocatore1.isEmpty()) {
                                // Passa alla schermata di gioco per il Giocatore 2
                                ChangePage(nomeGiocatore1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }

    public void ShowToast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomToast.showToast(context, text, Toast.LENGTH_SHORT);
            }
        });
    }

    public void ResetTxb() {
        txt_nome.setText("");
        txt_ip_server.setText("");
        txt_porta_server.setText("");
    }

    public void ChangePage(String nome_giocatore1) {
        Intent personaggi = new Intent(User_vs_User_connect_Activity.this, PersonaggiActivity.class);
        personaggi.putExtra("mod", modalita);
        personaggi.putExtra("nome1", nome_giocatore1);
        personaggi.putExtra("nome2", nome_giocatore2);
        personaggi.putExtra("attacco", false);
        //personaggi.putExtra("comms", comms);
        startActivity(personaggi);
    }

    public void btn_regole_pressed(View v) {
        btn_regole.startAnimation(scale_down);
        btn_regole.startAnimation(scale_up);
        regoleDialogNoGame.showDialog(this);
    }
}
