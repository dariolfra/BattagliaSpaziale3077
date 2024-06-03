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

class ClientThread extends ConnectionThread implements Parcelable {
    private String serverIp;
    private String nome_giocatore1, nome_giocatore2;
    private int serverPort;
    private String txtFromServer;
    private transient Socket client, client2;
    private User_vs_User_connect_Activity ClientActivity;
    private String serverName;
    private boolean primaConnessione;
    private boolean inviaMessaggio;
    private boolean riceviMessaggio;
    private String mess;
    private boolean gameEnded;

    public ClientThread(String nome, int sPort, String serverIp)
    {
        serverPort = sPort;
        serverName = serverIp;
        nome_giocatore1 = nome;
        primaConnessione = true;
        gameEnded = false;
    }

    protected ClientThread(Parcel in) {
        serverIp = in.readString();
        nome_giocatore1 = in.readString();
        nome_giocatore2 = in.readString();
        serverPort = in.readInt();
        txtFromServer = in.readString();
        serverName = in.readString();
        primaConnessione = in.readByte() != 0;
        inviaMessaggio = in.readByte() != 0;
        riceviMessaggio = in.readByte() != 0;
        mess = in.readString();
        gameEnded = in.readByte() != 0;
    }

    public static final Creator<ClientThread> CREATOR = new Creator<ClientThread>() {
        @Override
        public ClientThread createFromParcel(Parcel in) {
            return new ClientThread(in);
        }

        @Override
        public ClientThread[] newArray(int size) {
            return new ClientThread[size];
        }
    };

    public void startServer() {
        start();
    }

    @Override
    public void run()
    {
        while(!gameEnded)
        {
            try {
                if(primaConnessione)
                {
                    //serverName = ClientActivity.GetServerName();

                    /*if (nome_giocatore1.isEmpty() || serverName.isEmpty() || Optional.ofNullable(serverPort).orElse(0) == 0) {
                        throw new Exception();
                    }*/

                    client = new Socket(serverName, serverPort);

                    //scrittura su server
                    PrintWriter outputServer = new PrintWriter(client.getOutputStream(), true);
                    outputServer.flush();
                    outputServer.write(nome_giocatore1);
                    outputServer.flush();
                    outputServer.close();


                    //Toast.makeText(context, txtFromServer, Toast.LENGTH_SHORT).show();
                    //ClientActivity.ShowToast(nome_giocatore1);
                    //connessione_instaurata = true;

                    client2 = new Socket(serverName, serverPort);

                    BufferedReader sv_reader = new BufferedReader(new InputStreamReader(client2.getInputStream()));
                    nome_giocatore2 = sv_reader.readLine();
                    Log.i("CLIENT", nome_giocatore2);
                    Thread.sleep(100);
                    sv_reader.close();

                    ClientActivity.ResetTxb();
                    //ClientActivity.ChangePage();
                    //server.connessione_instaurata = true;
                    txtFromServer = "";
                    primaConnessione = false;


                }
                else
                {
                    if(inviaMessaggio)
                    {
                        Connect();
                        try {
                            PrintWriter outputServer = new PrintWriter(client.getOutputStream());
                            outputServer.flush();
                            outputServer.write(mess);
                            outputServer.flush();
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            //per ora ignoro il problema
                        }
                        mess = "";
                        inviaMessaggio = false;
                    }
                    else if(riceviMessaggio)
                    {
                        Connect();
                        try {
                            while(txtFromServer != "") {
                                BufferedReader sv_reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                txtFromServer = sv_reader.readLine();
                                Thread.sleep(100);
                                client.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            //per ora ignoro il problema
                            //toast
                        }
                        mess = "";
                        notifyAll();
                        riceviMessaggio = false;
                    }
                }
            } catch (IOException e) {
                ClientActivity.ShowToast("Connessione al server non riuscita");

                System.out.println(e);
                ClientActivity.ResetTxb();
                //connessione_instaurata = false;
            } catch (Exception e) {
                ClientActivity.ShowToast("Dati inseriti non validi");
                System.out.println(e);
                //connessione_instaurata = false;
            }
        }
    }

    public void SetActivity(User_vs_User_connect_Activity A)
    {
        ClientActivity = A;
    }

    public boolean Connect()
    {
        try {
            while(txtFromServer == "")
            {
                client = new Socket(serverName, serverPort);

                //lettura da server
                BufferedReader br_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                txtFromServer = br_input.readLine();
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

    public String Nome_G2(){
        return nome_giocatore2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(serverIp);
        dest.writeString(nome_giocatore1);
        dest.writeString(nome_giocatore2);
        dest.writeInt(serverPort);
        dest.writeString(txtFromServer);
        dest.writeString(serverName);
        dest.writeByte((byte) (primaConnessione ? 1 : 0));
        dest.writeByte((byte) (inviaMessaggio ? 1 : 0));
        dest.writeByte((byte) (riceviMessaggio ? 1 : 0));
        dest.writeString(mess);
        dest.writeByte((byte) (gameEnded ? 1 : 0));
    }

    @Override
    public void Abbandona()
    {
        InviaMessaggio("vittoria");
    }
}