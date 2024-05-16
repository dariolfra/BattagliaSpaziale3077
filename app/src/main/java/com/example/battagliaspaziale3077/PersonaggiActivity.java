package com.example.battagliaspaziale3077;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.Dictionary;
import java.util.HashMap;

public class PersonaggiActivity extends AppCompatActivity {

    ImageButton btn_pers_succ, btn_pers_prec;
    TextView lbl_descr_pers;
    ImageView img_personaggio;
    Button btn_seleziona_personaggio;
    HashMap<Integer, Drawable> indici_immagini;
    HashMap<Integer, String> indici_descrizione;
    int indice;
    Context context;
    String nome_personaggio;
    int modalita;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personaggi);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        context = this.getApplicationContext();

        Intent modalita = getIntent();
        nome_personaggio = modalita.getStringExtra("nome");
        this.modalita = modalita.getIntExtra("mod", 1);

        btn_pers_prec = (ImageButton) findViewById(R.id.btn_pers_prec);
        btn_pers_succ = (ImageButton) findViewById(R.id.btn_pers_succ);
        lbl_descr_pers = (TextView) findViewById(R.id.lbl_descr_pers);
        img_personaggio = (ImageView) findViewById(R.id.img_pers);
        btn_seleziona_personaggio = (Button) findViewById(R.id.btn_seleziona_personaggio);

        popola_hashmap_immagini();
        popola_hashmap_descrizioni();

        indice = 1;
        img_personaggio.setImageDrawable(indici_immagini.get(indice));
        lbl_descr_pers.setText(indici_descrizione.get(indice));

        btn_pers_succ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indice == 3){
                    indice = 1;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                }
                else{
                    indice++;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                }
            }
        });

        btn_pers_prec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indice == 1){
                    indice = 3;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                }
                else{
                    indice--;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                }
            }
        });

        btn_seleziona_personaggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gioco = new Intent(PersonaggiActivity.this, MainActivity.class);
                gioco.putExtra("personaggio", indice);
                gioco.putExtra("mod", modalita);
                gioco.putExtra("nome", nome_personaggio);
                startActivity(gioco);
            }
        });
    }

    public void popola_hashmap_immagini(){
        indici_immagini = new HashMap<>();
        indici_immagini.put(1, getResources().getDrawable(R.drawable.draghi, context.getTheme()));
        indici_immagini.put(2, getResources().getDrawable(R.drawable.salvini, context.getTheme()));
        indici_immagini.put(3, getResources().getDrawable(R.drawable.meloni, context.getTheme()));
    }

    public void popola_hashmap_descrizioni(){
        indici_descrizione = new HashMap<>();
        indici_descrizione.put(1, "grandissimo pisello");
        indici_descrizione.put(2, "grandissimo enorme mirabilante pisello");
        indici_descrizione.put(3, "grandissima bellissima fascistissima pisella");
    }
}
