package com.example.battagliaspaziale3077;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class User_vs_User_connect_Activity extends AppCompatActivity {
    TextInputEditText txt_nome, txt_ip_server, txt_porta_server;
    String serverName, nome_giocatore;
    int serverPort;
    Button btn_connettiti;
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

        btn_connettiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConnect();
            }
        });

    }

    public void onClickConnect(){
        nome_giocatore = txt_nome.getText().toString();
        serverName = txt_ip_server.getText().toString();
        serverPort = Integer.valueOf(txt_porta_server.getText().toString());

        txt_nome.setText("");
        txt_ip_server.setText("");
        txt_porta_server.setText("");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket client = new Socket(serverName, serverPort);
                    BufferedReader br_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String txtFromServer = br_input.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(txtFromServer);
                        }
                    });
                }catch (IOException e){
                    System.out.println(e.toString());
                }
            }
        }).start();
    }
}
