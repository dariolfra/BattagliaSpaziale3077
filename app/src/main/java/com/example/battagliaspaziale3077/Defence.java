package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Random;

public class Defence extends Game implements Serializable {
    //private Integer[] NaveIDs = new Integer[] {2131165441, 2131165439, 2131165440, 213116544, 2131165438, 2131165443};
    private Integer[] NaveIDs = new Integer[] {2131165441, 2131165439, 2131165440, 2131165442, 2131165438, 2131165443};
    private static HashMap<Integer, Integer> size_navi = new HashMap<Integer, Integer>();
    private static HashMap<Integer, List<Integer>> Navi = new HashMap<Integer, List<Integer>>();
    private static HashMap<Integer, List<Integer>> NaviColpite = new HashMap<Integer, List<Integer>>();
    private static HashMap<Integer, List<Integer>> NaviAffondate = new HashMap<Integer, List<Integer>>();
    private HashMap<Integer, Drawable> indici_personaggi;
    private ConnectionThread comms;
    private int id_pers;
    private int modalita;
    private String nome_giocatore1, nome_giocatore2;
    private boolean multiplayer;
    private ImageView background ,immagine_pers;
    private TextView giocatore1,giocatore2;
    private Context context;
    private Animation scale_down, scale_up;
    private static final int[] casellaColpita = new int[100];
    private static int[] tabella = new int[100];
    private GridAdapterDifesa gridAdapterDifesa;
    private static boolean attacco_ai_effettuato = false;
    private Button btn_torna_attacco;
    private static boolean colpo_a_segno = false;
    private static int casella_a_segno = 0;
    private static int colpi_a_segno_ai = 0;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defence);

        gridAdapterDifesa = new GridAdapterDifesa(this, tabella);
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapterDifesa);

        giocatore1 = (TextView) findViewById(R.id.txtNomeG1);
        giocatore2 = (TextView) findViewById(R.id.txtNomeG2);
        immagine_pers = (ImageView) findViewById(R.id.img_pers);
        background = (ImageView) findViewById(R.id.background);
        btn_torna_attacco = (Button) findViewById(R.id.btn_vai_attacco);
        context = this.getApplicationContext();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        background.setImageDrawable(getResources().getDrawable(R.drawable.background, context.getTheme()));

        Intent attack = getIntent();
        comms = (ConnectionThread) attack.getParcelableExtra("comms");
        Navi = (HashMap<Integer, List<Integer>>) attack.getSerializableExtra("Navi");
        NaviColpite = (HashMap<Integer, List<Integer>>) attack.getSerializableExtra("NaviColpite");
        NaviAffondate = (HashMap<Integer, List<Integer>>) attack.getSerializableExtra("NaviAffondate");
        colpo_a_segno = attack.getBooleanExtra("colpoasegno", false);
        id_pers = attack.getIntExtra("personaggio", 1);
        modalita = attack.getIntExtra("mod", 1);
        if (modalita == 1) {
            nome_giocatore1 = attack.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore2 = "AI";
            giocatore2.setText(nome_giocatore2);
            multiplayer = false;
        } else {
            nome_giocatore1 = attack.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore2 = attack.getStringExtra("nome2");
            giocatore2.setText(nome_giocatore2);
            multiplayer = true;
        }

        popola_personaggi();
        popola_size_navi();
        immagine_pers.setImageDrawable(indici_personaggi.get(id_pers));

        scale_down = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(context, R.anim.scale_up);

        if (NaviColpite.isEmpty()) {
            for(Integer i : NaveIDs){
                NaviColpite.put(i, new ArrayList<>());
                Log.i("COLPITE", i.toString());
            }
        }
        if (NaviAffondate.isEmpty()) {
            for(Integer i : NaveIDs){
                NaviAffondate.put(i, new ArrayList<>());
                Log.i("AFFONDATE", i.toString());
            }
        }

        AggiornaTabella();

        try {
            Gioca();
        } catch (InterruptedException e) {
            CustomToast.showToast(context, "Errore", Toast.LENGTH_SHORT);
        }

        btn_torna_attacco.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btn_torna_attacco.startAnimation(scale_down);
                btn_torna_attacco.startAnimation(scale_up);
                if(attacco_ai_effettuato){
                    Intent attack = new Intent(Defence.this, Attack.class);
                    attack.putExtra("comms",comms);
                    attack.putExtra("Navi", (Serializable) Navi);
                    attack.putExtra("defenceOrNot", true);
                    attack.putExtra("mod", modalita);
                    attack.putExtra("personaggio", id_pers);
                    attack.putExtra("nome1", nome_giocatore1);
                    attack.putExtra("NaviColpite", (Serializable) NaviColpite);
                    attack.putExtra("NaviAffondate", (Serializable) NaviAffondate);
                    if(multiplayer)
                    {
                        attack.putExtra("nome2", nome_giocatore2);
                    }
                    startActivity(attack);
                }
                else{
                    CustomToast.showToast(context, "ATTACCO DI AI NON EFFETTUATO", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        customDialog.showDialog(this);
    }

    public void Abbandona()
    {
        if(modalita != 1){
            comms.Abbandona();
        }
        Intent HA = new Intent(Defence.this, HomeActivity.class);
        startActivity(HA);
    }

    public void Gioca() throws InterruptedException {
        if(multiplayer)
        {
            String mess = AspettaMessaggio();
            Rispondi(mess);
            String done = "";
            while(done != "done")
            {
                done = AspettaMessaggio();
            }
        }
        else
        {
            int pos = genera_pos_attacco_ai_random();
            AttaccoAI(pos);
            attacco_ai_effettuato = true;
        }
        AggiornaTabella();
    }

    public void AttaccoAI(int posizione) {
        boolean colpita = false;
        for(Integer i : NaveIDs){
            List<Integer> posizioni_nave = Navi.get(i);
            for(int j : posizioni_nave){
                if(j == posizione){
                    tabella[posizione] = R.drawable.nave_colpita;
                    casellaColpita[posizione] = R.drawable.nave_colpita;
                    NaveColpita(i, posizione);
                    String s = NaveColpitaAffondata(i);
                    colpita = true;
                    colpo_a_segno = true;
                    casella_a_segno = posizione;
                    CustomToast2.showToast(context, "Bersaglio colpito da AI", Toast.LENGTH_SHORT);
                    Controllo_Fine_Gioco_AI();
                    break;
                }
            }
        }
        if(!colpita){
            tabella[posizione] = R.drawable.selected;
            CustomToast2.showToast(context, "Acqua di AI!", Toast.LENGTH_SHORT);
            colpo_a_segno = false;
            casella_a_segno = 0;
        }
    }

    public int genera_pos_attacco_ai_random(){
        Random rnd = new Random();
        int pos = -1;
        boolean corretta = false;
        while(!corretta){
            if(colpo_a_segno){
                pos = rnd.nextInt(casella_a_segno+21 - casella_a_segno-20) + casella_a_segno-20;
                Log.i("ATT", "range " + (casella_a_segno-20) + "-" + (casella_a_segno+21) + " pos " + pos);
            }
            else{
                pos = rnd.nextInt(100);
            }
            if(casellaColpita[pos] != R.drawable.nave_colpita && casellaColpita[pos] != R.drawable.selected && casellaColpita[pos] != R.drawable.x){
                corretta = true;
                casellaColpita[pos] = R.drawable.selected;
            }
        }
        return pos;
    }

    public void Attacco_Speciale_AI(){

    }

    public boolean Controllo_Fine_Gioco_AI(){
        boolean risultato = false;
        //PROBLEMA caselle che sono state colpite -> quindi rosse -> sovrascritte da selezionata
        //PROBLEMA problema riga 263, 264 ID non so come non combiacia
        //MANCA mossa speciale + controllo vittoria AI
        if(NaviAffondate.size() == 6){
            risultato = true;
            CustomToast2.showToast(context, "AI ha vinto!", Toast.LENGTH_SHORT);
        }
        return risultato;
    }

    public String AspettaMessaggio() throws InterruptedException {
        comms.RiceviRisposta();
        synchronized (comms){
            comms.wait(3000);
        }
        return comms.GetMessage();
    }

    public void Rispondi(String mess)
    {
        for (Integer i: NaveIDs)
        {
            for (Integer j : Navi.get(i))
            {
                if(j == Integer.valueOf(mess))
                {
                    List<Integer> pos = Navi.get(i);
                    pos.remove(i);
                    String answer;
                    if(pos.isEmpty())
                    {
                        answer = NaveColpitaAffondata(i);
                    }
                    else
                    {
                        NaveColpita(i, j);
                        answer = "colpita";
                    }
                    comms.InviaMessaggio(answer);
                    return;
                }
            }
        }
        comms.InviaMessaggio("acqua");
    }

    public void NaveColpita(Integer ID, Integer pos)
    {
        List<Integer> temp = NaviColpite.get(ID);
        Log.i("ID NAVE COLPITA", ID.toString());
        temp.add(pos);
        NaviColpite.put(ID, temp);
        List<Integer> posizioni = Navi.get(ID);
        for (Integer i : posizioni)
        {
            if (i == pos)
            {
                posizioni.remove(i);
                break;
            }
        }
    }

    public String NaveColpitaAffondata(Integer ID)
    {

        /*List<Integer> posizioni = NaviColpite.get(ID);
        for (int i = posizioni.size(); i >= 0; i--)
        {
            mess = mess + String.valueOf(posizioni.get(i));
            if(posizioni.size() > 1)
            {
                mess = mess + "-";
            }
            posizioni.remove(i);
        }

        NaviColpite.remove(ID);*/
        String mess = "colpita e affondata|";
        List<Integer> posizioni = NaviColpite.get(ID);
        int blocchi_nave_colpiti = 0;
        for(Integer i : posizioni){
            if(tabella[i] == R.drawable.nave_colpita){
                blocchi_nave_colpiti++;
            }
        }
        if(blocchi_nave_colpiti == size_navi.get(ID)){
            NaviColpite.remove(ID);
            NaviAffondate.put(ID, posizioni);
            CustomToast2.showToast(context, "Nave affondata da AI!", Toast.LENGTH_SHORT);
        }
        return mess;

    }

    public void AggiornaTabella() {
        for (Integer i : NaveIDs) {
            for (Integer pos : Navi.get(i)) {
                tabella[pos] = R.drawable.naveda1;
            }
        }
        if(!NaviColpite.isEmpty()){
            NaviColpite.forEach((k, v) -> {
                for (Integer pos : v) {
                    tabella[pos] = R.drawable.nave_colpita;
                }
            });
        }
        if(!NaviAffondate.isEmpty()){
            NaviAffondate.forEach((k, v) -> {
                for (Integer pos : v) {
                    tabella[pos] = R.drawable.x;
                }
            });
        }
        gridAdapterDifesa.notifyDataSetChanged();
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

    public void popola_size_navi(){
        size_navi.put(2131165441, 2);
        size_navi.put(2131165439, 4);
        size_navi.put(2131165440, 4);
        size_navi.put(2131165442, 4);
        size_navi.put(2131165438, 3);
        size_navi.put(2131165443, 5);
    }
}
