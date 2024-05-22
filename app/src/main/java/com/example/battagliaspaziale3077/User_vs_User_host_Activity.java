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
    }

    public void onClickStartServer(View view){
        nome_giocatore = String.valueOf(txt_nome_giocatore.getText());
        serverThread = new ServerThread(nome_giocatore, serverPort);
        serverThread.SetActivity(this);
        serverThread.startServer();
    }

    public void SetAddressPort(String sIP, int sPort)
    {
        lbl_ip_sv_box.setText(sIP);
        lbl_porta_sv_box.setText(String.valueOf(sPort));
    }

    public void onClickStopServer(View view)
    {
        serverThread.StopServer();
    }

    public void ChangeLabelText(String message)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lbl_conn_box.setText(message);
            }
        });
    }

    public void ChangePage()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShowToast("Nome Giocatore : " + nome_giocatore + " /Mod : " + modalita);
                Intent personaggi = new Intent(User_vs_User_host_Activity.this, PersonaggiActivity.class);
                personaggi.putExtra("mod", modalita);
                personaggi.putExtra("nome",nome_giocatore);
                personaggi.putExtra("comms", ServerThread.class);
                startActivity(personaggi);
            }
        });
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
}
