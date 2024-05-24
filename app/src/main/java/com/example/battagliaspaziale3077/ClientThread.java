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

class ClientThread extends ConnectionThread implements Runnable {
    private String serverIp;
    private String nome_giocatore1, nome_giocatore2;
    private int serverPort;
    private String txtFromServer;
    private Socket client;
    private User_vs_User_connect_Activity ClientActivity;
    private String serverName;
    private boolean primaConnessione;
    private boolean inviaMessaggio;
    private boolean riceviMessaggio;
    private String mess;
    private boolean gameEnded;

    public ClientThread(String nome, int sPort)
    {
        serverPort = sPort;
        nome_giocatore1 = nome;
        primaConnessione = true;
        gameEnded = false;
    }

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
                    serverName = ClientActivity.GetServerName();

                    if (nome_giocatore1.isEmpty() || serverName.isEmpty() || Optional.ofNullable(serverPort).orElse(0) == 0) {
                        throw new Exception();
                    }

                    client = new Socket(serverName, serverPort);

                    //lettura da server
                    BufferedReader br_input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    txtFromServer = br_input.readLine();
                    nome_giocatore2 = txtFromServer;

                    //scrittura su server
                    PrintWriter outputServer = new PrintWriter(client.getOutputStream());
                    outputServer.flush();
                    outputServer.write(nome_giocatore1);
                    outputServer.flush();

                    //Toast.makeText(context, txtFromServer, Toast.LENGTH_SHORT).show();
                    ClientActivity.ShowToast(txtFromServer);
                    //connessione_instaurata = true;

                    ClientActivity.ResetTxb();
                    ClientActivity.ChangePage();
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
}