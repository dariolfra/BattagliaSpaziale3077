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
import java.net.Socket;
import java.util.Optional;

public class User_vs_User_connect_Activity extends AppCompatActivity {
    TextInputEditText txt_nome, txt_ip_server, txt_porta_server;
    String serverName, nome_giocatore;
    int serverPort;
    Button btn_connettiti;
    int modalita = 2;
    boolean connessione_instaurata;
    Context context;
    public boolean server_ha_scritto;
    Animation scale_down, scale_up;
    Socket client;
    String txtFromServer;

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

    public void onClickConnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    nome_giocatore = txt_nome.getText().toString();
                    serverName = txt_ip_server.getText().toString();
                    serverPort = Integer.valueOf(txt_porta_server.getText().toString());

                    if(nome_giocatore.isEmpty() || serverName.isEmpty() || Optional.ofNullable(serverPort).orElse(0) == 0 ){
                        throw new Exception();
                    }

                    client = new Socket(serverName, serverPort);

                    //lettura da server
                    BufferedReader br_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    txtFromServer = br_input.readLine();

                    //scrittura su server
                    PrintWriter outputServer = new PrintWriter(client.getOutputStream());
                    outputServer.flush();
                    outputServer.write("ciao da client");
                    outputServer.flush();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(context, txtFromServer, Toast.LENGTH_SHORT).show();
                            CustomToast.showToast(context, txtFromServer, Toast.LENGTH_SHORT);
                            //connessione_instaurata = true;

                            txt_nome.setText("");
                            txt_ip_server.setText("");
                            txt_porta_server.setText("");

                            Intent personaggi = new Intent(User_vs_User_connect_Activity.this, PersonaggiActivity.class);
                            personaggi.putExtra("mod", modalita);
                            personaggi.putExtra("nomeg1", nome_giocatore);
                            personaggi.putExtra("nomeg2", txtFromServer);
                            startActivity(personaggi);
                            //server.connessione_instaurata = true;

                        }
                    });

                }catch (IOException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.showToast(context, "Connessione al server non riuscita", Toast.LENGTH_SHORT);
                        }
                    });

                    System.out.println(e);
                    txt_ip_server.setText("");
                    txt_porta_server.setText("");
                    //connessione_instaurata = false;
                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.showToast(context, "Dati inseriti non validi", Toast.LENGTH_SHORT);
                        }
                    });

                    System.out.println(e);
                    //connessione_instaurata = false;
                }
            }
        }).start();

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

    public String GetName()
    {
        return txt_nome.getText().toString();
    }

    public String GetServerName()
    {
        return txt_ip_server.getText().toString();
    }

    public int GetServerPort()
    {
        return Integer.valueOf(txt_porta_server.getText().toString());
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
        personaggi.putExtra("nome", nome_giocatore);
        startActivity(personaggi);
    }
}
