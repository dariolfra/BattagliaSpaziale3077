package com.example.battagliaspaziale3077;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.*;


class ServerThread extends ConnectionThread implements Parcelable {
    private boolean serverRunning;
    private transient ServerSocket serverSocket;
    private int count = 0;
    private String nome_giocatore1, nome_giocatore2;
    private int serverPort;
    private String serverIP;
    private transient Socket client, client2;
    private User_vs_User_host_Activity HostActivity;
    private String clientIP;
    private String mess;
    private String txt_from_client;
    private boolean primaConnessione;
    private boolean inviaMessaggio;
    private boolean riceviMessaggio;

    public ServerThread(String nome, int sPort)
    {
        this.serverPort = sPort;
        this.nome_giocatore1 = nome;
        this.primaConnessione = true;
        this.inviaMessaggio = false;
        this.riceviMessaggio= false;
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
    protected ServerThread(Parcel in) {
        serverRunning = in.readByte() != 0;
        count = in.readInt();
        nome_giocatore1 = in.readString();
        nome_giocatore2 = in.readString();
        serverPort = in.readInt();
        serverIP = in.readString();
        clientIP = in.readString();
        mess = in.readString();
        txt_from_client = in.readString();
        primaConnessione = in.readByte() != 0;
        inviaMessaggio = in.readByte() != 0;
        riceviMessaggio = in.readByte() != 0;
    }


    public static final Creator<ServerThread> CREATOR = new Creator<ServerThread>() {
        @Override
        public ServerThread createFromParcel(Parcel in) {
            return new ServerThread(in);
        }

        @Override
        public ServerThread[] newArray(int size) {
            return new ServerThread[size];
        }
    };

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
                    HostActivity.ChangeLabelText("Dispositivo :\n" + clientIP + "\n connesso");



                    BufferedReader br_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    nome_giocatore2 = br_input.readLine();
                    Log.i("SERVER", nome_giocatore2);
                    br_input.close();

                    //HostActivity.ShowToast("nome host " + nome_giocatore1 + " nome client " + nome_giocatore2);

                    client.close();

                    client2 = serverSocket.accept();

                    PrintWriter outputServer = new PrintWriter(client2.getOutputStream(), true);
                    outputServer.write(nome_giocatore1);
                    Log.i("SERVER", "MESSAGGIO INVIATO");
                    outputServer.close();
                    client2.close();

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
                            notifyAll();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (serverRunning ? 1 : 0));
        dest.writeInt(count);
        dest.writeString(nome_giocatore1);
        dest.writeString(nome_giocatore2);
        dest.writeInt(serverPort);
        dest.writeString(serverIP);
        dest.writeString(clientIP);
        dest.writeString(mess);
        dest.writeString(txt_from_client);
        dest.writeByte((byte) (primaConnessione ? 1 : 0));
        dest.writeByte((byte) (inviaMessaggio ? 1 : 0));
        dest.writeByte((byte) (riceviMessaggio ? 1 : 0));
    }

    @Override
    public void Abbandona()
    {
        InviaMessaggio("vittoria");
    }
}