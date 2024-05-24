package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Attack extends Activity {

    /*Button btn_attacca, btn_attacco_speciale;
    TextView nome_g1, nome_g2;
    String nome_giocatore1, nome_giocatore2;
    int modalita;
    int id_pers;*/
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);

        /*Intent gioco = getIntent();
        id_pers = gioco.getIntExtra("personaggio", 1);
        modalita = gioco.getIntExtra("mod", 1);
        if(modalita == 1){
            nome_giocatore1 = gioco.getStringExtra("nomeg1");
            nome_giocatore2 = "AI";
        }
        else{
            nome_giocatore2 = gioco.getStringExtra("nomeg1");
            nome_giocatore1 = gioco.getStringExtra("nomeg2");
        }*/

        //andare avanti
    }
    private int[] immaginiCasella;
    private int[] casellaColpita;
    private int[] countNavi;

    public Attack(int[] img)
    {
        immaginiCasella = img;
        countNavi = new int[99];
        casellaColpita = new int[99];
    }

    public void ClickedButton(View view)
    {

    }

    public boolean canAttack(int pos)
    {
        if (casellaColpita[pos] != 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean Attaco(int pos)
    {
        if(immaginiCasella[pos] == 0)
        {
            casellaColpita[pos] = 1;
            return false;
        }
        else
        {
            int nave = immaginiCasella[pos];
            casellaColpita[pos] = 2;
            countNavi[nave]++;
            if(countNavi[nave] == nave)
            {
                NaveAffondata(nave);
            }
            return true;
        }
    }

    public void NaveAffondata(int nave)
    {
        for(int i = 0; i < immaginiCasella.length; i++)
        {
            if(immaginiCasella[i] == nave)
            {
                casellaColpita[i] = 3;
                immaginiCasella[i] = -nave;
            }
        }
    }
}
