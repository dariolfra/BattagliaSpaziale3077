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

class ServerThread extends Thread implements Runnable {
    private boolean serverRunning;
    private ServerSocket serverSocket;
    private int count = 0;
    private String nome_giocatore;
    private int serverPort;
    private String serverIP;
    private Socket client;
    private User_vs_User_host_Activity HostActivity;
    private String clientIP;

    public ServerThread(String nome, int sPort)
    {
        serverPort = sPort;
        nome_giocatore = nome;
        try {
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
        } catch (Exception ignored) { } // for now eat exceptions
    }

    public void startServer() {
        start();
    }

    @Override
    public void run() {
        try {
            if(nome_giocatore.isEmpty()){
                throw new Exception();
            }
            else{
                serverRunning = true;
            }
            HostActivity.SetAddressPort(serverIP, serverPort);
            serverSocket = new ServerSocket(serverPort);
            HostActivity.ChangeLabelText("Aspettando una connessione");

            while(serverRunning) {
                client = serverSocket.accept();
                count++;
                clientIP = String.valueOf(client.getInetAddress());
                HostActivity.ChangeLabelText("Dispositivo " + clientIP + " connesso");

                PrintWriter outputServer = new PrintWriter(client.getOutputStream(), true);
                outputServer.write(nome_giocatore);
                Log.i("SERVER", "MESSAGGIO INVIATO");
                //client.server_ha_scritto = true;


                client.close();
                HostActivity.ChangePage();
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
            serverSocket = new ServerSocket(serverPort);
            int i = 0;
            while(serverRunning && i  < 10000) {
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
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //per ora ignoro il problema
            //toast
        }
        return false;
    }

    public String InviaAttacco(String messaggio)
    {
        try {
            if(!serverRunning){
                throw new Exception();
            }
            serverSocket = new ServerSocket(serverPort);

            PrintWriter outputServer = new PrintWriter(client.getOutputStream(), true);
            outputServer.write(messaggio);
            Log.i("SERVER", "MESSAGGIO INVIATO");
            client.close();
            count--;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //per ora ignoro il problema
        }
        if(Connect())
        {
            return RiceviRisposta();
        }
        else
        {
            return "ERRORE DI CONNESSIONE";
            //toast
        }
    }

    public String RiceviRisposta()
    {
        String txt_from_client = "ERRORE: NO MESSAGGIO";
        try {
            if(!serverRunning){
                throw new Exception();
            }
            serverSocket = new ServerSocket(serverPort);

            while(serverRunning) {
                BufferedReader sv_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                txt_from_client = sv_reader.readLine();
                Thread.sleep(100);/*
                synchronized (this){
                    Log.i("CLIENT", "STO ASPETTANDO");
                    client.wait();
                }*/
                client.close();
                count--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //per ora ignoro il problema
            //toast
        }
        return txt_from_client;
    }
}