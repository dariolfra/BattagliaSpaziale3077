package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.HashMap;

public class Attack extends Activity {
    private int id_pers, modalita;
    private String nome_giocatore1, nome_giocatore2;
    private ConnectionThread comms;
    private Context context;
    private HashMap<Integer, Drawable> indici_mossaspeciale;
    private HashMap<Integer, Drawable> indici_personaggi;
    private Button btn_attacca, btn_att_speciale;
    private TextView giocatore1, giocatore2;
    private ImageView immagine_pers, img_mossa_speciale;
    private CustomToast customToast;
    private int pos;
    private boolean multiplayer;
    private int[] casellaColpita;
    ImageView background;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);

        context = this.getApplicationContext();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        btn_attacca = (Button) findViewById(R.id.attacco);
        btn_att_speciale = (Button) findViewById(R.id.btn_attacco_speciale);
        giocatore1 = (TextView) findViewById(R.id.txtNomeG1);
        giocatore2 = (TextView) findViewById(R.id.txtNomeG2);
        immagine_pers = (ImageView) findViewById(R.id.img_pers);
        img_mossa_speciale = (ImageView) findViewById(R.id.img_mossa_speciale);
        background = (ImageView) findViewById(R.id.background);

        background.setImageDrawable(getResources().getDrawable(R.drawable.background, context.getTheme()));

        Intent gioco = getIntent();
        id_pers = gioco.getIntExtra("personaggio", 1);
        modalita = gioco.getIntExtra("mod", 1);
        comms = (ConnectionThread) gioco.getSerializableExtra("comms");

        if (modalita == 1) {
            nome_giocatore1 = gioco.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore2 = "AI";
            giocatore2.setText(nome_giocatore2);
            multiplayer = false;
        } else {
            nome_giocatore2 = gioco.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore1 = gioco.getStringExtra("nome2");
            giocatore2.setText(nome_giocatore2);
            multiplayer = true;
        }

        popola_mosse_speciale();
        popola_personaggi();
        immagine_pers.setImageDrawable(indici_personaggi.get(id_pers));

        GridAdapterAttacco gridAdapterAttacco = new GridAdapterAttacco(this, casellaColpita);
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapterAttacco);
        context = this.getApplicationContext();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //restituisce la posizione della cella cliccata
                //Toast.makeText(Attack.this, position, Toast.LENGTH_SHORT).show();
                pos = position;
                canAttack();
            }
        });
    }

    public Attack()
    {
        casellaColpita = new int[99];
    }

    public void Attacca_G2(View view) throws InterruptedException {
        if(canAttack())
        {
            if(multiplayer)
            {
                comms.InviaMessaggio(String.valueOf(pos));
                comms.RiceviRisposta();
                comms.wait();
                Attacco(comms.GetMessage());
            }
            else
            {
                //gestisco dopo
            }
        }
        else
        {
            //deseleziono la casella
            customToast.showToast(context, "non puoi attaccare questa casella", Toast.LENGTH_SHORT);
        }
    }

    public boolean canAttack()
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

    public void Attacco(String result)
    {
        if(result == "colpito")
        {
            casellaColpita[pos] = 2;
        }
        else if(result == "acqua")
        {
            casellaColpita[pos] = 1;
        }
        else //esempio stringa: "colpita e affondata|coordinata1-coordinata2-coordinata3..."
        {
            NaveColpitaEAffondata(result.split("|")[1]);
        }
    }

    public void NaveColpitaEAffondata(String pos)
    {
        String[] posizioni = pos.split("-");
        for (String s: posizioni)
        {
            casellaColpita[Integer.valueOf(s)] = 3;
        }
    }

    public void popola_mosse_speciale()
    {
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

    public void popola_personaggi()
    {
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

    public void genera_img_mossa_speciale(View view) throws InterruptedException {
        img_mossa_speciale.setImageDrawable(indici_mossaspeciale.get(id_pers));

    }

    public void CambiaImmagini()
    {

    }
}
