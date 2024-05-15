package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    Button btn_start_sv, btn_stop_sv;
    String serverIP = "192.168.1.103"; //mettere quello del proprio telefono
    int serverPort = 42069; //>1023 no porte riservate
    private ServerThread serverThread;
    int modalita = 3;
    String txt_from_client;
    Boolean finito_con_successo;

    Context context;

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
                finito_con_successo = false;
                serverSocket = new ServerSocket(serverPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lbl_conn_box.setText("Aspettando una connessione");
                    }
                });

                while(serverRunning)
                {
                    Socket socket = serverSocket.accept();
                    count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lbl_conn_box.setText("Dispositivo " + socket.getInetAddress() + " connesso, numero : " + count);
                        }
                    });

                    PrintWriter outputServer = new PrintWriter(socket.getOutputStream());
                    outputServer.write("ciao da server");
                    outputServer.flush();
                    Thread.sleep(100);

                    BufferedReader sv_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    txt_from_client = sv_reader.readLine();
                    Thread.sleep(100);

                    socket.close();
                }
                finito_con_successo = true;
            } catch (IOException e) {
                e.printStackTrace();
            }catch (InterruptedException e){

            }
            if(finito_con_successo){
                Toast.makeText(context, txt_from_client, Toast.LENGTH_SHORT).show();
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
