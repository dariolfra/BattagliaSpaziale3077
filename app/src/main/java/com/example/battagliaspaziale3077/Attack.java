package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

public class Attack extends Activity {
    int id_pers, modalita;
    String nome_giocatore1, nome_giocatore2;
    private ConnectionThread comms;
    Context context;
    HashMap<Integer, Drawable> indici_mossaspeciale;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);

        context = this.getApplicationContext();

        Intent gioco = getIntent();
        id_pers = gioco.getIntExtra("personaggio", 1);
        modalita = gioco.getIntExtra("mod", 1);
        if(modalita == 1){
            nome_giocatore1 = gioco.getStringExtra("nomeg1");
            nome_giocatore2 = "AI";
        }
        else{
            nome_giocatore2 = gioco.getStringExtra("nomeg1");
            nome_giocatore1 = gioco.getStringExtra("nomeg2");
        }

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
        casellaColpita[pos] = 1;
        return false;
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

    public void popola_mosse_speciale(){
        indici_mossaspeciale = new HashMap<>();
        indici_mossaspeciale.put(1, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(2, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(3, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(4, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(5, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(6, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(7, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(8, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(9, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_mossaspeciale.put(10, getResources().getDrawable(R.drawable.blur, context.getTheme()));
    }
}
