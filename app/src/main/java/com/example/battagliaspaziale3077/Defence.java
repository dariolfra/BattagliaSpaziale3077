package com.example.battagliaspaziale3077;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.HashMap;

import java.util.List;

public class Defence extends Game implements Serializable {
    private Integer[] NaveIDs = new Integer[]{2131165441, 2131165439, 2131165440, 213116544, 2131165438, 2131165443};
    private HashMap<Integer, List<Integer>> Navi;
    private HashMap<Integer, List<Integer>> NaviColpite;
    private HashMap<Integer, List<Integer>> NaviAffondate;
    private HashMap<Integer, Drawable> indici_personaggi;
    private ConnectionThread comms;
    private int id_pers;
    private int modalita;
    private String nome_giocatore1, nome_giocatore2;
    private boolean multiplayer;
    private ImageView background ,immagine_pers;
    private TextView giocatore1,giocatore2;
    private Context context;
    private int[] casellaColpita;
    private int[] tabella;
    private GridAdapterDifesa gridAdapterDifesa;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defence);

        tabella = new int[100];
        gridAdapterDifesa = new GridAdapterDifesa(this, tabella);
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapterDifesa);

        giocatore1 = (TextView) findViewById(R.id.txtNomeG1);
        giocatore2 = (TextView) findViewById(R.id.txtNomeG2);
        immagine_pers = (ImageView) findViewById(R.id.img_pers);
        background = (ImageView) findViewById(R.id.background);

        context = this.getApplicationContext();

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        background.setImageDrawable(getResources().getDrawable(R.drawable.background, context.getTheme()));

        Intent attack = getIntent();
        comms = (ConnectionThread) attack.getParcelableExtra("comms");
        Navi = (HashMap<Integer, List<Integer>>) attack.getSerializableExtra("Navi");
        NaviColpite = (HashMap<Integer, List<Integer>>) attack.getSerializableExtra("NaviColpite");
        NaviAffondate = (HashMap<Integer, List<Integer>>) attack.getSerializableExtra("NaviAffondata");
        id_pers = attack.getIntExtra("personaggio", 1);
        modalita = attack.getIntExtra("mod", 1);
        casellaColpita = attack.getIntArrayExtra("casellaColpita");
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
        immagine_pers.setImageDrawable(indici_personaggi.get(id_pers));


        if (NaviColpite == null) {
            NaviColpite = new HashMap<Integer, List<Integer>>();
        }
        if (NaviAffondate == null) {
            NaviAffondate = new HashMap<Integer, List<Integer>>();
        }

        AggiornaTabella();

        try {
            Gioca();
        } catch (Exception e) {
            CustomToast.showToast(context, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        customDialog.showDialog(this);
    }

    public void Abbandona()
    {
        comms.Abbandona();
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
            //gestico più tardi
        }
        Intent attack = new Intent(Defence.this, Attack.class);
        attack.putExtra("comms",comms);
        attack.putExtra("Navi", (Serializable) Navi);
        attack.putExtra("defenceOrNot", true);
        attack.putExtra("casellaColpita", casellaColpita);
        attack.putExtra("mod", modalita);
        attack.putExtra("personaggio", id_pers);
        attack.putExtra("nome1", nome_giocatore1);
        attack.putExtra("NaviColpite", (Serializable) NaviColpite);
        if(multiplayer)
        {
            attack.putExtra("nome2", nome_giocatore2);
        }
        AggiornaTabella();
        startActivity(attack);
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
        temp.add(pos);
        NaviColpite.put(ID, temp);
        List<Integer> posizioni = Navi.get(ID);
        for (Integer i : posizioni)
        {
            if (posizioni.get(i) == pos)
            {
                posizioni.remove(i);
                break;
            }
        }
    }

    public String NaveColpitaAffondata(Integer ID)
    {
        String mess = "colpita e affondata|";
        List<Integer> posizioni = NaviColpite.get(ID);
        for (int i = posizioni.size(); i >= 0; i--)
        {
            mess = mess + String.valueOf(posizioni.get(i));
            if(posizioni.size() > 1)
            {
                mess = mess + "-";
            }
            posizioni.remove(i);
        }
        NaviColpite.remove(ID);
        return mess;
    }

    public void AggiornaTabella()
    {
        for (int i = 0; i < NaveIDs.length; i++) {
            for (Integer pos : Navi.get(NaveIDs[i])) {
                tabella[pos] = R.drawable.naveda1;
            }
        }
        for (int i = 0; i < NaviColpite.size(); i++) {
            for (Integer pos : NaviColpite.get(i)) {
                tabella[pos] = R.drawable.nave_colpita;
            }
        }
        for (int i = 0; i < NaviAffondate.size(); i++) {
            for (Integer pos : NaviAffondate.get(i)) {
                tabella[pos] = R.drawable.x;
            }
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
}
