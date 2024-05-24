package com.example.battagliaspaziale3077;

import android.app.Activity;
import android.os.Bundle;

import java.util.HashMap;

import java.util.List;

public class Defence extends Activity {
    private Integer[] NaveIDs = new Integer[]{2131165441, 2131165439, 2131165440, 213116544, 2131165438, 2131165443};
    private HashMap<Integer, List<Integer>> Navi;
    private HashMap<Integer, List<Integer>> NaviColpite;
    private ConnectionThread comms;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void AspettaMessaggio() throws InterruptedException {
        comms.RiceviRisposta();
        comms.wait();
        String mess = comms.GetMessage();
        Rispondi(mess);
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
