package com.example.battagliaspaziale3077;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.Optional;

public class User_vs_User_connect_Activity extends AppCompatActivity implements Serializable{
    TextInputEditText txt_nome, txt_ip_server, txt_porta_server;
    String serverName, nome_giocatore1, nome_giocatore2;
    int serverPort;
    Button btn_connettiti;
    int modalita = 2;
    boolean connessione_instaurata;
    Context context;
    public boolean server_ha_scritto;
    Animation scale_down, scale_up;
    Socket client;
    String txtFromServer;
    private ClientThread comms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_user_connect);

        btn_connettiti = (Button) findViewById(R.id.btn_connettiti);
        txt_nome = (TextInputEditText) findViewById(R.id.txt_input_nome);
        txt_ip_server = (TextInputEditText) findViewById(R.id.txt_input_ip_server);
        txt_porta_server = (TextInputEditText) findViewById(R.id.txt_input_porta_server);

        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));
        context = this.getApplicationContext();

        btn_connettiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_connettiti.startAnimation(scale_down);
                btn_connettiti.startAnimation(scale_up);
                onClickConnect();
            }
        });
    }
    public void onClickConnect()  {
        try{
            nome_giocatore1 = txt_nome.getText().toString();
            serverName = txt_ip_server.getText().toString();
            serverPort = Integer.valueOf(txt_porta_server.getText().toString());
            if (nome_giocatore1.isEmpty() || serverName.isEmpty() || Optional.ofNullable(serverPort).orElse(0) == 0) {
                throw new Exception();
            }
            else {
                comms = new ClientThread(nome_giocatore1, serverPort, serverName);
                comms.SetActivity(this);
                comms.start();
            }
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CustomToast.showToast(context, "Inserisci tutti i valori", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    public void ShowToast(String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomToast.showToast(context, text, Toast.LENGTH_SHORT);
            }
        });
    }

    public void ResetTxb()
    {
        txt_nome.setText("");
        txt_ip_server.setText("");
        txt_porta_server.setText("");
    }

    public void ChangePage()
    {
        Intent personaggi = new Intent(User_vs_User_connect_Activity.this, PersonaggiActivity.class);
        personaggi.putExtra("mod", modalita);
        personaggi.putExtra("nome1", nome_giocatore1);
        personaggi.putExtra("nome2", comms.Nome_G2());
        personaggi.putExtra("attacco", false);
        //personaggi.putExtra("comms", (Serializable) comms);
        startActivity(personaggi);
    }
}
