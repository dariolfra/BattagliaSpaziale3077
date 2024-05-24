package com.example.battagliaspaziale3077;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.*;

class ServerThread extends ConnectionThread implements Serializable {
    private boolean serverRunning;
    private ServerSocket serverSocket;
    private int count = 0;
    private String nome_giocatore1,nome_giocatore2;
    private int serverPort;
    private String serverIP;
    private Socket client;
    private User_vs_User_host_Activity HostActivity;
    private String clientIP;
    private String mess;
    private String txt_from_client;
    private boolean primaConnessione;
    private boolean inviaMessaggio;
    private boolean riceviMessaggio;

    public ServerThread(String nome, int sPort)
    {
        serverPort = sPort;
        nome_giocatore1 = nome;
        primaConnessione = true;
        inviaMessaggio = false;
        riceviMessaggio= false;
        /*try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (isIPv4)
                        {
                            serverIP = sAddr;
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions*/


    }

    public void startServer() {
        serverRunning = true;
        start();
    }

    @Override
    public void run() {
        try {
            while(serverRunning) {
                if(primaConnessione)
                {
                    if(nome_giocatore1.isEmpty()){
                        Log.i("SERVER", "NOME GIOCATORE 1 VUOTO");
                        throw new Exception();
                    }
                    //HostActivity.SetAddressPort(serverIP, serverPort);
                    serverSocket = new ServerSocket(serverPort);
                    HostActivity.ChangeLabelText("Aspettando una connessione");

                    client = serverSocket.accept();
                    count++;
                    clientIP = String.valueOf(client.getInetAddress());
                    HostActivity.ChangeLabelText("Dispositivo " + clientIP + " connesso");

                    /*PrintWriter outputServer = new PrintWriter(client.getOutputStream(), true);
                    outputServer.write(nome_giocatore1);
                    Log.i("SERVER", "MESSAGGIO INVIATO");
                    outputServer.close();*/

                    BufferedReader br_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    nome_giocatore2 = br_input.readLine();
                    Log.i("SERVER", "MESSAGGIO RICEVUTO");
                    br_input.close();

                    client.close();
                    HostActivity.ChangePage();
                    primaConnessione = false;
                }
                else
                {
                    if(inviaMessaggio)
                    {
                        if(Connect())
                        {
                            try {
                                if(!serverRunning){
                                    throw new Exception();
                                }

                                PrintWriter outputServer = new PrintWriter(client.getOutputStream(), true);
                                outputServer.write(mess);
                                Log.i("SERVER", "MESSAGGIO INVIATO");
                                client.close();
                                count--;

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                //per ora ignoro il problema
                            }
                        }
                        else
                        {
                            //per ora ignoro l'errore
                        }
                        inviaMessaggio = false;
                    }
                    else if(riceviMessaggio)
                    {
                        if(Connect())
                        {
                            try {
                                if(!serverRunning){
                                    throw new Exception();
                                }
                                while(serverRunning) {
                                    BufferedReader sv_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                    txt_from_client = sv_reader.readLine();
                                    nome_giocatore2 = txt_from_client;
                                    Thread.sleep(100);
                                    client.close();
                                    count--;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                //per ora ignoro il problema
                                //toast
                            }
                            riceviMessaggio = false;
                        }
                        else
                        {
                            txt_from_client = "ERRORE DI CONNESSIONE";
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            HostActivity.ShowToast("Nome Giocatore non inserito");
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
                        HostActivity.ChangeLabelText("Connessione server chiusa");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void SetActivity(User_vs_User_host_Activity A){
        HostActivity = A;
    }

    public boolean isServerRunning(){
        return serverRunning;
    }

    public boolean Connect()
    {
        try {
            if(!serverRunning){
                throw new Exception();
            }
            client = serverSocket.accept();
            count++;
            if(String.valueOf(client.getInetAddress()) == clientIP)
            {
                return true;
            }
            else
            {
                client.close();
                count--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //per ora ignoro il problema
            //toast
        }
        return false;
    }

    public void InviaMessaggio(String messaggio)
    {
        inviaMessaggio = true;
        mess = messaggio;
    }

    public void RiceviRisposta()
    {
        riceviMessaggio = true;
    }

    public String GetMessage()
    {
        String temp = txt_from_client;
        txt_from_client = "";
        return temp;
    }

    public String Nome_G2()
    {
        return nome_giocatore2;
    }
}