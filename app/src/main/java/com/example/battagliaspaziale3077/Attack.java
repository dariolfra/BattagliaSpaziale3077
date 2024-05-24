package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Attack extends Activity {

    private ConnectionThread comms;
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
}
