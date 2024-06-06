package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class User_vs_User_host_Activity extends AppCompatActivity implements Serializable {
    TextView lbl_ip_sv, lbl_ip_sv_box, lbl_porta_sv, lbl_porta_sv_box, lbl_conn, lbl_conn_box;
    TextInputEditText txt_nome_giocatore;
    Button btn_start_sv, btn_stop_sv, btn_regole;
    String serverIP = "192.168.55.249"; //mettere quello del proprio telefono
    int serverPort = 12345; //>1023 no porte riservate
    private ServerThread serverThread;
    int modalita = 3;
    String nome_giocatore1, nome_giocatore2;
    ;
    String txt_from_client;
    Context context;
    Socket client;
    //User_vs_User_connect_Activity client = new User_vs_User_connect_Activity();
    public boolean connessione_instaurata;
    Animation scale_up, scale_down;
    TextView lbl_codiceConn;
    ConnectionFirebase connectionFirebase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_user_host);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        context = this.getApplicationContext();

        lbl_conn = (TextView) findViewById(R.id.lbl_connessioni);
        lbl_conn_box = (TextView) findViewById(R.id.lbl_connessioni_box);
        btn_start_sv = (Button) findViewById(R.id.btn_start_server);
        btn_stop_sv = (Button) findViewById(R.id.btn_stop_server);
        txt_nome_giocatore = (TextInputEditText) findViewById(R.id.txt_nome_giocatore);
        btn_regole = (Button) findViewById(R.id.btn_regole);


        lbl_codiceConn = findViewById(R.id.lbl_codiceDiConn);
        connectionFirebase = new ConnectionFirebase();

        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_up);

    }

    public void onClickStartServer(View view) {
        btn_start_sv.startAnimation(scale_down);
        btn_start_sv.startAnimation(scale_up);

        nome_giocatore1 = String.valueOf(txt_nome_giocatore.getText());
        if (nome_giocatore1.isEmpty()) {
            CustomToast.showToast(context, "INSERISCI UN NOME!", Toast.LENGTH_SHORT);
        } else {
            //stringa per creare il codice della partita
            String codiceConn = generaTreLettere() + generaTreNumeri();
            //metodo per creare la partita

            connectionFirebase.CreaPartita(codiceConn, nome_giocatore1, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Verifica se il nome del giocatore 2 è stato impostato
                    String nomeGiocatore2 = snapshot.child("nomeGiocatore2").getValue(String.class);
                    if (nomeGiocatore2 != null && !nomeGiocatore2.isEmpty()) {
                        // Passa alla schermata di gioco per il Giocatore 1
                        ChangePage(nomeGiocatore2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            lbl_codiceConn.setText(codiceConn);
        }
    }

    public void onClickStopServer(View view) {
        btn_stop_sv.startAnimation(scale_down);
        btn_stop_sv.startAnimation(scale_up);
        if (serverThread != null) {
            if (serverThread.isServerRunning()) {
                serverThread.StopServer();
            }
        } else {
            CustomToast.showToast(context, "STANZA NON CREATA!", Toast.LENGTH_SHORT);
        }
    }

    public void ChangeLabelText(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lbl_conn_box.setText(message);
            }
        });
    }

    public void ChangePage(String nome_giocatore2) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent personaggi = new Intent(User_vs_User_host_Activity.this, PersonaggiActivity.class);
                personaggi.putExtra("mod", modalita);
                personaggi.putExtra("nome1", nome_giocatore1);
                personaggi.putExtra("nome2", nome_giocatore2);
                personaggi.putExtra("attacco", true);
                //personaggi.putExtra("comms", serverThread);
                startActivity(personaggi);
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

    public void btn_regole_pressed(View v) {
        btn_regole.startAnimation(scale_down);
        btn_regole.startAnimation(scale_up);
        regoleDialogNoGame.showDialog(this);
    }

    public static String generaTreLettere() {
        Random random = new Random();
        StringBuilder lettere = new StringBuilder(3);

        for (int i = 0; i < 3; i++) {
            char lettera = (char) (random.nextInt(26) + 'A'); // Genera una lettera maiuscola casuale
            lettere.append(lettera);
        }
        return lettere.toString();
    }

    public static String generaTreNumeri() {
        Random random = new Random();
        StringBuilder numeri = new StringBuilder(3);

        for (int i = 0; i < 3; i++) {
            int numero = random.nextInt(10); // Genera un numero da 0 a 9
            numeri.append(numero);
        }

        return numeri.toString();
    }
}
