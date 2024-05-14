package com.example.battagliaspaziale3077;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class User_vs_User_host_Activity extends AppCompatActivity {
    TextView lbl_ip_sv, lbl_ip_sv_box, lbl_porta_sv, lbl_porta_sv_box, lbl_conn, lbl_conn_box;
    Button btn_start_sv, btn_stop_sv;
    private String serverIP = "192.168.1.102"; //mettere quello del proprio telefono
    private int serverPort = 42069; //>1023 no porte riservate

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_user_host);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        lbl_ip_sv = (TextView) findViewById(R.id.lbl_server_ip);
        lbl_ip_sv_box = (TextView) findViewById(R.id.lbl_server_ip_box);
        lbl_porta_sv = (TextView) findViewById(R.id.lbl_porta_server);
        lbl_porta_sv_box = (TextView) findViewById(R.id.lbl_server_porta_box);
        lbl_conn = (TextView) findViewById(R.id.lbl_connessioni);
        lbl_conn_box = (TextView) findViewById(R.id.lbl_connessioni_box);
        btn_start_sv = (Button) findViewById(R.id.btn_start_server);
        btn_stop_sv = (Button) findViewById(R.id.btn_stop_server);

        lbl_ip_sv_box.setText(serverIP);
        lbl_porta_sv_box.setText(serverPort);
    }

    private ServerThread serverThread;

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
                serverSocket = new ServerSocket(serverPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lbl_conn_box.setText("waiting for other player");
                    }
                });

                while(serverRunning)
                {
                    Socket socket = serverSocket.accept();
                    count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lbl_conn_box.setText("connected " + socket.getInetAddress() + " " + count);
                        }
                    });

                    PrintWriter outputServer = new PrintWriter(socket.getOutputStream());
                    outputServer.write("nigga");
                    outputServer.flush();

                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
                                    lbl_conn_box.setText("connection stopped");
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
