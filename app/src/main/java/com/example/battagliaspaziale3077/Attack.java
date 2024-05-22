package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Attack extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);
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
