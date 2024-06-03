package com.example.battagliaspaziale3077;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class ConnectionFirebase {
    private DatabaseReference databaseReference;

    public void inviaHashMapFormazione(/*HashMap<Integer, List<Integer>> formazione*/){
        databaseReference.setValue("dfmdmf");
    }
    public void CreaPartita(String codice, String nomeGiocatore1,ValueEventListener listener) {
        //inizializzo l'istanza
        if(nomeGiocatore1 != ""){
            FirebaseDatabase instance = FirebaseDatabase.getInstance();
            databaseReference = instance.getReference(codice);

            Map<String, Object> data = new HashMap<>();
            data.put("nomeGiocatore1", nomeGiocatore1);
            data.put("nomeGiocatore2", "");

            databaseReference.setValue(data);
            databaseReference.addValueEventListener(listener);
        }
        else {

        }

    }
    public void unisciAPartita(String codice, String nomeGiocatore2, ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        databaseReference = instance.getReference(codice);
        databaseReference.addListenerForSingleValueEvent(listener);
        // Aggiunge un listener per verificare se il codice della partita esiste
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Se la partita esiste, aggiorna solo il campo nomeGiocatore2
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("nomeGiocatore2", nomeGiocatore2);
                    databaseReference.updateChildren(updates);
                } else {
                    // Se la partita non esiste, gestisci l'errore
                    System.err.println("Partita non trovata: " + codice);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gestisci l'errore
                System.err.println("Errore durante il recupero dei dati: " + databaseError.getMessage());
            }
        });    }

    public void personaggioGiocatore2() {
        // Verifica se la HashMap "personaggio scelto" è già stata creata
        if (databaseReference == null) {
            FirebaseDatabase instance = FirebaseDatabase.getInstance();
            databaseReference = instance.getReference().child("personaggio scelto");

            // Creazione dei dati per la HashMap
            Map<String, Object> data = new HashMap<>();
            data.put("nomeGiocatore1", "");
            data.put("nomeGiocatore2", true);

            // Imposta i dati nella HashMap
            databaseReference.setValue(data);
        } else {
            // La HashMap "personaggio scelto" è già stata creata
            // Quindi aggiorniamo solo il campo "nomeGiocatore2" a true
            databaseReference.child("nomeGiocatore2").setValue(true);
        }
    }

    public void personaggioGiocatore1() {
        // Verifica se la HashMap "personaggio scelto" è già stata creata
        if (databaseReference == null) {
            FirebaseDatabase instance = FirebaseDatabase.getInstance();
            databaseReference = instance.getReference().child("personaggio scelto");

            // Creazione dei dati per la HashMap
            Map<String, Object> data = new HashMap<>();
            data.put("nomeGiocatore1", true);
            data.put("nomeGiocatore2", "");

            // Imposta i dati nella HashMap
            databaseReference.setValue(data);
        } else {
            // La HashMap "personaggio scelto" è già stata creata
            // Quindi aggiorniamo solo il campo "nomeGiocatore2" a true
            databaseReference.child("nomeGiocatore1").setValue(true);
        }
    }
}
