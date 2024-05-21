package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class User_vs_User_host_Activity extends AppCompatActivity {
    TextView lbl_ip_sv, lbl_ip_sv_box, lbl_porta_sv, lbl_porta_sv_box, lbl_conn, lbl_conn_box;
    TextInputEditText txt_nome_giocatore;
    Button btn_start_sv, btn_stop_sv;
    String serverIP = "192.168.55.139"; //mettere quello del proprio telefono
    int serverPort = 42069; //>1023 no porte riservate
    private ServerThread serverThread;
    int modalita = 3;
    String nome_giocatore;
    String txt_from_client;
    Context context;
    Socket client;
    //User_vs_User_connect_Activity client = new User_vs_User_connect_Activity();
    public  boolean connessione_instaurata;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_user_host);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        context = this.getApplicationContext();

        lbl_ip_sv = (TextView) findViewById(R.id.lbl_server_ip);
        lbl_ip_sv_box = (TextView) findViewById(R.id.lbl_server_ip_box);
        lbl_porta_sv = (TextView) findViewById(R.id.lbl_porta_server);
        lbl_porta_sv_box = (TextView) findViewById(R.id.lbl_server_porta_box);
        lbl_conn = (TextView) findViewById(R.id.lbl_connessioni);
        lbl_conn_box = (TextView) findViewById(R.id.lbl_connessioni_box);
        btn_start_sv = (Button) findViewById(R.id.btn_start_server);
        btn_stop_sv = (Button) findViewById(R.id.btn_stop_server);
        txt_nome_giocatore = (TextInputEditText) findViewById(R.id.txt_nome_giocatore);

        lbl_ip_sv_box.setText(serverIP);
        lbl_porta_sv_box.setText(String.valueOf(serverPort));
    }

    public void onClickStartServer(View view){
        serverThread = new ServerThread();
        serverThread.startServer();
    }

    public void onClickStopServer(View view)
    {
        serverThread.StopServer();
    }

    class ServerThread extends Thread implements Runnable {
        private boolean serverRunning;
        private ServerSocket serverSocket;
        private int count = 0;

        public void startServer() {
            serverRunning = true;
            start();
        }

        @Override
        public void run() {
            try {
                nome_giocatore = txt_nome_giocatore.getText().toString();
                if(nome_giocatore.isEmpty()){
                    throw new Exception();
                }
                serverSocket = new ServerSocket(serverPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lbl_conn_box.setText("Aspettando una connessione");
                    }
                });

                while(serverRunning) {
                    client = serverSocket.accept();
                    count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lbl_conn_box.setText("Dispositivo " + client.getInetAddress() + " connesso");
                        }
                    });

                    PrintWriter outputServer = new PrintWriter(client.getOutputStream(), true);
                    outputServer.write("ciao da server");
                    Log.i("SERVER", "MESSAGGIO INVIATO");
                    //client.server_ha_scritto = true;

                    //BufferedReader sv_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //txt_from_client = sv_reader.readLine();

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, txt_from_client, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    //Thread.sleep(100);
                    //synchronized (this){
                    //    Log.i("CLIENT", "STO ASPETTANDO");
                    //    client.wait();
                    //}
                    client.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.showToast(context, "Nome Giocatore : " + nome_giocatore + " /Mod : " + modalita, Toast.LENGTH_SHORT);
                            Intent personaggi = new Intent(User_vs_User_host_Activity.this, PersonaggiActivity.class);
                            personaggi.putExtra("mod", modalita);
                            personaggi.putExtra("nome",nome_giocatore);
                            startActivity(personaggi);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                CustomToast.showToast(context, "Nome Giocatore non inserito", Toast.LENGTH_SHORT);
            }
        }

        public void StopServer()
        {
            serverRunning = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(serverSocket != null)
                    {
                        try {
                            serverSocket.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lbl_conn_box.setText("Connessione server chiusa");
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}
