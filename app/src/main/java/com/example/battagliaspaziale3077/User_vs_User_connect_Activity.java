package com.example.battagliaspaziale3077;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

    Socket client;

    //User_vs_User_host_Activity server = new User_vs_User_host_Activity();
    String txtFromServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_user_connect);

        btn_connettiti = (Button) findViewById(R.id.btn_connettiti);
        txt_nome = (TextInputEditText) findViewById(R.id.txt_input_nome);
        txt_ip_server = (TextInputEditText) findViewById(R.id.txt_input_ip_server);
        txt_porta_server = (TextInputEditText) findViewById(R.id.txt_input_porta_server);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));
        context = this.getApplicationContext();

        btn_connettiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Log.i("CLIENT", "MESSAGGIO LETTO : " + txtFromServer);

                    //scrittura su server
//                    PrintWriter outputServer = new PrintWriter(client.getOutputStream());
//                    outputServer.flush();
//                    outputServer.write("ciao da client");
//                    outputServer.flush();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, txtFromServer, Toast.LENGTH_SHORT).show();

                            //connessione_instaurata = true;

                            txt_nome.setText("");
                            txt_ip_server.setText("");
                            txt_porta_server.setText("");

                            Intent gioco = new Intent(User_vs_User_connect_Activity.this, PersonaggiActivity.class);
                            gioco.putExtra("mod", modalita);
                            gioco.putExtra("nome", nome_giocatore);
                            startActivity(gioco);
                            //server.connessione_instaurata = true;
                        }
                    });
                }catch (IOException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Connessione al server non riuscita", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "Dati inseriti non validi", Toast.LENGTH_SHORT).show();
                        }
                    });

                    System.out.println(e);
                    //connessione_instaurata = false;
                }
            }
        }).start();

    }
}
