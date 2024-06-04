package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serial;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class PersonaggiActivity extends AppCompatActivity implements Serializable {

    ImageButton btn_pers_succ, btn_pers_prec;
    TextView lbl_descr_pers, lbl_abilita_pers;
    ImageView img_personaggio;
    Button btn_seleziona_personaggio, btn_regole;
    HashMap<Integer, Drawable> indici_immagini;
    HashMap<Integer, String> indici_descrizione;
    HashMap<Integer, String> indici_abilita;
    ConnectionThread comms;
    int indice;
    Context context;
    String nome_g1, nome_g2;
    int modalita;
    MediaPlayer mp;
    Animation scale_down, scale_up;
    ConnectionFirebase connectionFirebase;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personaggi);

        connectionFirebase = new ConnectionFirebase();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        context = this.getApplicationContext();

        Intent mod = getIntent();
        modalita = mod.getIntExtra("mod", 1);
        //comms = (ConnectionThread) mod.getParcelableExtra("comms");
        boolean attacco = mod.getBooleanExtra("attacco", true);
        if (modalita == 1) {
            nome_g1 = mod.getStringExtra("nome");
            nome_g2 = "AI";
        } else {
            nome_g1 = mod.getStringExtra("nome1");
            nome_g2 = mod.getStringExtra("nome2");
        }

        btn_pers_prec = (ImageButton) findViewById(R.id.btn_pers_prec);
        btn_pers_succ = (ImageButton) findViewById(R.id.btn_pers_succ);
        lbl_descr_pers = (TextView) findViewById(R.id.lbl_descr_pers);
        lbl_abilita_pers = (TextView) findViewById(R.id.lbl_abilita);
        img_personaggio = (ImageView) findViewById(R.id.img_pers);
        btn_seleziona_personaggio = (Button) findViewById(R.id.btn_seleziona_personaggio);
        btn_regole = (Button) findViewById(R.id.btn_regole);

        scale_down = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(context, R.anim.scale_up);

        popola_hashmap_immagini();
        popola_hashmap_descrizioni();
        popola_hasmap_abilita();

        indice = 1;
        img_personaggio.setImageDrawable(indici_immagini.get(indice));
        lbl_descr_pers.setText(indici_descrizione.get(indice));
        lbl_abilita_pers.setText(indici_abilita.get(indice));

        btn_pers_succ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_pers_succ.startAnimation(scale_down);
                btn_pers_succ.startAnimation(scale_up);
                if (indice == 10) {
                    indice = 1;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                    lbl_abilita_pers.setText(indici_abilita.get(indice));
                } else {
                    indice++;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                    lbl_abilita_pers.setText(indici_abilita.get(indice));
                }
            }
        });

        btn_pers_prec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_pers_prec.startAnimation(scale_down);
                btn_pers_prec.startAnimation(scale_up);
                if (indice == 1) {
                    indice = 10;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                    lbl_abilita_pers.setText(indici_abilita.get(indice));
                } else {
                    indice--;
                    img_personaggio.setImageDrawable(indici_immagini.get(indice));
                    lbl_descr_pers.setText(indici_descrizione.get(indice));
                    lbl_abilita_pers.setText(indici_abilita.get(indice));
                }
            }
        });

        btn_seleziona_personaggio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modalita != 1) {
                    if (modalita == 2) {
                        //quello che si unisce alla partita

                        //bisogna sistemare l'inserimento nel db e far aspettare che abbiano finito entrambi
                        connectionFirebase.personaggioGiocatore2(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Verifica se il campo PersonaggioGiocatore2 è stato impostato
                                String personaggioGiocatore1 = snapshot.child("PersonaggioGiocatore1").getValue(String.class);
                                if (personaggioGiocatore1.equals("true")) {
                                    // Passa alla schermata di gioco per il Giocatore 2
                                    ChangePage(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Gestisci l'errore del database
                                System.err.println("Errore del database: " + error.getMessage());
                            }
                        });
                        if (connectionFirebase.PersonaggioG1() == "true") {
                            ChangePage(false);
                        }

                    } else if (modalita == 3) {
                        //quello che la crea
                        connectionFirebase.personaggioGiocatore1(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Verifica se il nome del giocatore 1 è stato impostato
                                String PersonaggioGiocatore2 = snapshot.child("PersonaggioGiocatore2").getValue(String.class);
                                if (PersonaggioGiocatore2.equals("true")) {
                                    // Passa alla schermata di gioco per il Giocatore 2
                                    ChangePage(true);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (connectionFirebase.PersonaggioG2() == "true") {
                            ChangePage(true);
                        }

                    }
                } else {
                    ChangePage(true);
                }
                // } catch (InterruptedException e) {
                //ignoro l'errore
                // }
                btn_seleziona_personaggio.startAnimation(scale_down);
                btn_seleziona_personaggio.startAnimation(scale_up);
                Intent gioco = new Intent(PersonaggiActivity.this, MainActivity.class);
                gioco.putExtra("personaggio", indice);
                gioco.putExtra("mod", modalita);
                gioco.putExtra("nome1", nome_g1);
                gioco.putExtra("nome2", nome_g2);
                // gioco.putExtra("comms", comms);
                gioco.putExtra("attacco", attacco);
                suono_personaggio(indice);
                startActivity(gioco);
            }
        });
        // } catch (InterruptedException e) {
        //ignoro l'errore
        // }
    }

    public void btn_regole_pressed(View v) {
        btn_regole.startAnimation(scale_down);
        btn_regole.startAnimation(scale_up);
        regoleDialogNoGame.showDialog(this);
    }

    public void popola_hashmap_immagini() {
        indici_immagini = new HashMap<>();
        indici_immagini.put(1, getResources().getDrawable(R.drawable.blur, context.getTheme()));
        indici_immagini.put(2, getResources().getDrawable(R.drawable.meloni, context.getTheme()));
        indici_immagini.put(3, getResources().getDrawable(R.drawable.erbrasiliano, context.getTheme()));
        indici_immagini.put(4, getResources().getDrawable(R.drawable.ciccio, context.getTheme()));
        indici_immagini.put(5, getResources().getDrawable(R.drawable.marzone, context.getTheme()));
        indici_immagini.put(6, getResources().getDrawable(R.drawable.optimusprime, context.getTheme()));
        indici_immagini.put(7, getResources().getDrawable(R.drawable.papa, context.getTheme()));
        indici_immagini.put(8, getResources().getDrawable(R.drawable.peffo, context.getTheme()));
        indici_immagini.put(9, getResources().getDrawable(R.drawable.shiva, context.getTheme()));
        indici_immagini.put(10, getResources().getDrawable(R.drawable.panda, context.getTheme()));
    }

    public void popola_hashmap_descrizioni() {
        indici_descrizione = new HashMap<>();
        indici_descrizione.put(1, "Giammarco Tocco aka Blur è uno streamer italiano, è in grado di creare contenuti orginali e divertenti senza l'utilizzo di termini come bestemmie o senza il riciclo di contenuti altrui. E' un grande amico di TheRealMarzaa.");
        indici_descrizione.put(2, "Giorgia Meloni è l'attuale presidente del consiglio in italia, è a capo del governo e combina la grinta di un leone con la determinazione di un bambino che vuole un gelato a dicembre. Al suo fianco amici fedeli come Matteo Salvini e Silvio Berlusconi ormai scomparso.");
        indici_descrizione.put(3, "Er Brasiliano, è un personaggio italiano conosciuto per il suo corpo quasi completamente tatuato e la sua calvizia, noto inoltre anche per la sua posizione sul web definita da alcuni con stampa fascista. E' infine un gymbro ineguagliabile.");
        indici_descrizione.put(4, "Cicciogamer89 è uno youtuber e streamer italiano, conosciuto per i suoi deliziosi CiccioBurgher, si identifica come il Capobranco dei suoi paguri,ed è infine il protagonista del chest opening di Clash Royale più grande d'Italia '104mila gemme signori e signore'.");
        indici_descrizione.put(5, "Francesco Marzano aka TheRealMarzaa è uno youtuber e streamer italiano, ha origini baresi e il suo accento barese è molto spiccato, dimostra un carattere tranquillo e possiede due cani a cui dimostra sempre amore e affetto.");
        indici_descrizione.put(6, "Optimus Prime, l'ultimo della sua specie, possiede tecnologie Autobot avanzate e inisieme ai sui fedeli compagni Autobot ha salvato il mondo, dispone di una potenza di fuoco incommensurabile e grandi abilità di combattimento corpo a corpo.");
        indici_descrizione.put(7, "Papa Francesco, potenfice italiano e figura importante e indispensabile per i suoi fedeli, la sua unica e infinatemente potente arma e questa gli permette di riuscire nei suoi scopi di pontefice. Originario dell'Argentina ma ormai inseparabile dall'italia.");
        indici_descrizione.put(8, "Dario Moccia è uno youtuber e streamer italiano, ha una grande passione per manga e anime affronta le situazioni con tranquillità e spensieratezza, dispone di una grande cultura e di frasi iconiche dei suoi canali.");
        indici_descrizione.put(9, "Andrea Arrigoni aka Shiva è un rapper italiano noto per la sua carrierra discografica, cresciuta molto negli ultimi anni, ha creato canzoni importanti e con un significato profonodo come Syrup, Take 4 e Fendi Belt.");
        indici_descrizione.put(10, "Po, protagonista dei diversi film intitolati Kung Fu Panda, è un panda figlio adottivo di un'oca, sin da piccolo dimostra un'immenso appetito e farebbe di tutto per il cibo, in lui risiede una forza ineguagliabe e viene soprannominato Il Guerriero Dragone.");
    }

    public void popola_hasmap_abilita() {
        indici_abilita = new HashMap<>();
        indici_abilita.put(1, "La sua abilità speciale è neutralizzare le flotte nemiche attraverso l'urlo degli stalloni.");
        indici_abilita.put(2, "La sua abilità speciale è sconfiggere le flotte nemiche attraverso una politica perfetta e impeccabile.");
        indici_abilita.put(3, "La sua abilità speciale è sfondare le flotte nemiche sulle note di 'Volevo te... pensavo solo a te e e...'");
        indici_abilita.put(4, "La sua abilità speciale è quella di debellare le flotte nemiche attraverso un suo nemico storico il cornetto alla nutella.");
        indici_abilita.put(5, "La sua abilità speciale è annichilire le flotte nemiche attraverso il dialetto barese.");
        indici_abilita.put(6, "La sua abilità speciale è devastare le flotte nemiche combattendo affiano ai suoi fedeli Autobots.");
        indici_abilita.put(7, "La sua abilità speciale è appacificare le flotte nemiche attraverso la sua fede e la parola di Dio.");
        indici_abilita.put(8, "La sua abilità speciale e annientare le flotte nemiche a colpi di PEFFORZAAA e SI VA A LETTOOO.");
        indici_abilita.put(9, "La sua abilità speciale è sterminare le flotte nemiche attraverso la sua mira infallibile.");
        indici_abilita.put(10, "La sua abilità speciale è sbaragliare le flotte nemiche attraverso il suo Kung Fu.");
    }

    public void suono_personaggio(int indice) {
        if (indice == 1) {
            mp = MediaPlayer.create(context, R.raw.blur);
            mp.start();
        } else if (indice == 2) {
            mp = MediaPlayer.create(context, R.raw.meloni);
            mp.start();
        } else if (indice == 3) {
            mp = MediaPlayer.create(context, R.raw.brasiliano);
            mp.start();
        } else if (indice == 4) {
            mp = MediaPlayer.create(context, R.raw.ciccio);
            mp.start();
        } else if (indice == 5) {
            mp = MediaPlayer.create(context, R.raw.marza);
            mp.start();
        } else if (indice == 6) {
            mp = MediaPlayer.create(context, R.raw.optimusprime);
            mp.start();
        } else if (indice == 7) {
            mp = MediaPlayer.create(context, R.raw.papa);
            mp.start();
        } else if (indice == 8) {
            mp = MediaPlayer.create(context, R.raw.pefforza);
            mp.start();
        } else if (indice == 9) {
            mp = MediaPlayer.create(context, R.raw.shiva);
            mp.start();
        } else if (indice == 10) {
            mp = MediaPlayer.create(context, R.raw.kungfupanda);
            mp.start();
        }

    }

    public void ChangePage(boolean attacco) {
        btn_seleziona_personaggio.startAnimation(scale_down);
        btn_seleziona_personaggio.startAnimation(scale_up);
        Intent gioco = new Intent(PersonaggiActivity.this, MainActivity.class);
        gioco.putExtra("personaggio", indice);
        gioco.putExtra("mod", modalita);
        gioco.putExtra("nome1", nome_g1);
        gioco.putExtra("nome2", nome_g2);
        // gioco.putExtra("comms", comms);
        gioco.putExtra("attacco", attacco);
        suono_personaggio(indice);
        startActivity(gioco);
    }
}