package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class Attack extends Activity {
    int id_pers, modalita;
    String nome_giocatore1, nome_giocatore2;
    private ConnectionThread comms;
    Context context;
    HashMap<Integer, Drawable> indici_mossaspeciale;
    HashMap<Integer, Drawable> indici_personaggi;
    Button btn_attacca, btn_att_speciale;
    TextView giocatore1, giocatore2;
    ImageView immagine_pers, img_mossa_speciale;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);

        context = this.getApplicationContext();

        btn_attacca = (Button) findViewById(R.id.attacco);
        btn_att_speciale = (Button) findViewById(R.id.btn_attacco_speciale);
        giocatore1 = (TextView) findViewById(R.id.txtNomeG1);
        giocatore2 = (TextView) findViewById(R.id.txtNomeG2);
        immagine_pers = (ImageView) findViewById(R.id.img_pers);
        img_mossa_speciale = (ImageView) findViewById(R.id.img_mossa_speciale);

        Intent gioco = getIntent();
        id_pers = gioco.getIntExtra("personaggio", 1);
        modalita = gioco.getIntExtra("mod", 1);
        if(modalita == 1){
            nome_giocatore1 = gioco.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore2 = "AI";
            giocatore2.setText(nome_giocatore2);
        }
        else{
            nome_giocatore2 = gioco.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore1 = gioco.getStringExtra("nome2");
            giocatore2.setText(nome_giocatore2);
        }

        popola_mosse_speciale();
        popola_personaggi();

        immagine_pers.setImageDrawable(indici_personaggi.get(id_pers));

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

    public void Attacca_G2(View view)
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
        indici_mossaspeciale.put(1, getResources().getDrawable(R.drawable.attaccoserpente, context.getTheme()));
        indici_mossaspeciale.put(2, getResources().getDrawable(R.drawable.attaccot, context.getTheme()));
        indici_mossaspeciale.put(3, getResources().getDrawable(R.drawable.attaccos, context.getTheme()));
        indici_mossaspeciale.put(4, getResources().getDrawable(R.drawable.attaccotratteggio, context.getTheme()));
        indici_mossaspeciale.put(5, getResources().getDrawable(R.drawable.attaccoscala, context.getTheme()));
        indici_mossaspeciale.put(6, getResources().getDrawable(R.drawable.attaccospada, context.getTheme()));
        indici_mossaspeciale.put(7, getResources().getDrawable(R.drawable.attaccoda5, context.getTheme()));
        indici_mossaspeciale.put(8, getResources().getDrawable(R.drawable.attacox, context.getTheme()));
        indici_mossaspeciale.put(9, getResources().getDrawable(R.drawable.attaccos, context.getTheme()));
        indici_mossaspeciale.put(10, getResources().getDrawable(R.drawable.attaccot, context.getTheme()));
    }

    public void popola_personaggi(){
        indici_personaggi = new HashMap<>();
        indici_personaggi.put(1, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_personaggi.put(2, getResources().getDrawable(R.drawable.meloni, context.getTheme()));
        indici_personaggi.put(3, getResources().getDrawable(R.drawable.erbrasiliano, context.getTheme()));
        indici_personaggi.put(4, getResources().getDrawable(R.drawable.ciccio, context.getTheme()));
        indici_personaggi.put(5, getResources().getDrawable(R.drawable.marzone, context.getTheme()));
        indici_personaggi.put(6, getResources().getDrawable(R.drawable.optimusprime, context.getTheme()));
        indici_personaggi.put(7, getResources().getDrawable(R.drawable.papa, context.getTheme()));
        indici_personaggi.put(8, getResources().getDrawable(R.drawable.peffo, context.getTheme()));
        indici_personaggi.put(9, getResources().getDrawable(R.drawable.shiva, context.getTheme()));
        indici_personaggi.put(10, getResources().getDrawable(R.drawable.panda, context.getTheme()));
    }

    public void genera_img_mossa_speciale(){
        img_mossa_speciale.setImageDrawable(indici_mossaspeciale.get(id_pers));
    }
}
