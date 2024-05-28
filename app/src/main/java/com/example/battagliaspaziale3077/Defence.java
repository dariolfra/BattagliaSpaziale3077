package com.example.battagliaspaziale3077;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;

import java.util.List;

public class Defence extends Game {
    private Integer[] NaveIDs = new Integer[]{2131165441, 2131165439, 2131165440, 213116544, 2131165438, 2131165443};
    private HashMap<Integer, List<Integer>> Navi;
    private HashMap<Integer, List<Integer>> NaviColpite;
    private HashMap<Integer, List<Integer>> NaviAffondate;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defence);


        tabella = new int[100];
        GridAdapterDifesa gridAdapterDifesa = new GridAdapterDifesa(this, tabella);
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapterDifesa);

        giocatore1 = (TextView) findViewById(R.id.txtNomeG1);
        giocatore2 = (TextView) findViewById(R.id.txtNomeG2);
        immagine_pers = (ImageView) findViewById(R.id.img_pers);
        background = (ImageView) findViewById(R.id.background);

        context = this.getApplicationContext();

        background.setImageDrawable(getResources().getDrawable(R.drawable.background, context.getTheme()));

        Intent attack = getIntent();
        comms = (ConnectionThread) attack.getSerializableExtra("comms");
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
            nome_giocatore2 = attack.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore1 = attack.getStringExtra("nome2");
            giocatore2.setText(nome_giocatore2);
            multiplayer = true;
        }

        if (NaviColpite == null) {
            NaviColpite = new HashMap<Integer, List<Integer>>();
        }
        if (NaviAffondate == null) {
            NaviAffondate = new HashMap<Integer, List<Integer>>();
        }

        for (int i = 0; i < NaveIDs.length; i++) {
            for (Integer pos : Navi.get(NaveIDs[i])) {
                tabella[pos] = R.drawable.naveda1;
                gridAdapterDifesa.notifyDataSetChanged();
            }
        }
        for (int i = 0; i < NaviColpite.size(); i++) {
            for (Integer pos : NaviColpite.get(i)) {
                tabella[pos] = R.drawable.nave_colpita;
                gridAdapterDifesa.notifyDataSetChanged();
            }
        }
        for (int i = 0; i < NaviAffondate.size(); i++) {
            for (Integer pos : NaviAffondate.get(i)) {
                tabella[pos] = R.drawable.x;
                gridAdapterDifesa.notifyDataSetChanged();
            }
        }
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
        attack.putExtra("comms", (Serializable) comms);
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
        startActivity(attack);
    }

    public String AspettaMessaggio() throws InterruptedException {
        comms.RiceviRisposta();
        comms.wait();
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
}
