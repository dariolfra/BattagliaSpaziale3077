package com.example.battagliaspaziale3077;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ConnectionFirebase {
    private DatabaseReference databaseReference;
    private static String codiceConn;
    private static String personaggioG1;
    private static String personaggioG2;
    private static String formazioneG1;
    private static String formazioneG2;
    private Defence defence;
    public ConnectionFirebase() {
        defence = new Defence();
    }
    public void inviaHashMapFormazione(int modalità, ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = instance.getReference(codiceConn);
        if (modalità == 2) {
            // Aggiorna Formazioneg2
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("Formazioneg2", "true");
                        formazioneG2 = "true";
                        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Verifica se Formazioneg1 è "true"
                                databaseReference.addListenerForSingleValueEvent(listener);
                            }
                        });
                    } else {
                        System.err.println("Partita non trovata: " + codiceConn);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Errore durante il recupero dei dati: " + databaseError.getMessage());
                }
            });
        } else if (modalità == 3) {
            // Aggiorna Formazioneg1
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("Formazioneg1", "true");
                        formazioneG1 = "true";
                        databaseReference.updateChildren(updates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Verifica se Formazioneg2 è "true"
                                databaseReference.addListenerForSingleValueEvent(listener);
                            }
                        });
                    } else {
                        System.err.println("Partita non trovata: " + codiceConn);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Errore durante il recupero dei dati: " + databaseError.getMessage());
                }
            });
        }
    }


    public void CreaPartita(String codice, String nomeGiocatore1, ValueEventListener listener) {
        //inizializzo l'istanza
        if (!Objects.equals(nomeGiocatore1, "")) {
            FirebaseDatabase instance = FirebaseDatabase.getInstance();
            databaseReference = instance.getReference(codice);

            //metto in codice dell'istanza
            codiceConn = codice;


            //dichiaro le variabili
            Map<String, Object> data = new HashMap<>();
            data.put("nomeGiocatore1", nomeGiocatore1);
            data.put("nomeGiocatore2", "");
            data.put("PersonaggioGiocatore1", "");
            data.put("PersonaggioGiocatore2", "");
            data.put("Formazioneg1", "");
            data.put("Formazioneg2", "");
            data.put("azioneg1", -1);
            data.put("azioneg2",-1);
            data.put("rispostag1",-1); //quando g2 attacca lui scrive 0 se la nave non è colpita o id nave
            data.put("rispostag2",-1);

            databaseReference.setValue(data);

            databaseReference.addValueEventListener(listener);

        } else {

        }

    }

    public void unisciAPartita(String codice, String nomeGiocatore2, ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = instance.getReference(codice);
        codiceConn = codice;
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
        });
    }


    public void personaggioGiocatore2(ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = instance.getReference(codiceConn);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Se la partita esiste, aggiorna solo il campo PersonaggioGiocatore2
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("PersonaggioGiocatore2", "true");
                    personaggioG2 = "true";
                    databaseReference.updateChildren(updates);
                } else {
                    // Se la partita non esiste, gestisci l'errore
                    System.err.println("Partita non trovata: " + codiceConn);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gestisci l'errore del database
                System.err.println("Errore del database: " + databaseError.getMessage());
            }
        });

        // Attacca il listener fornito per gestire ulteriori azioni basate sul valore aggiornato
        databaseReference.addValueEventListener(listener);
    }


    public void personaggioGiocatore1(ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        databaseReference = instance.getReference(codiceConn);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Se la partita esiste, aggiorna solo il campo PersonaggioGiocatore1
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("PersonaggioGiocatore1", "true");
                    databaseReference.updateChildren(updates);
                } else {
                    // Se la partita non esiste, gestisci l'errore
                    System.err.println("Partita non trovata: " + codiceConn);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gestione dell'errore, se necessario
            }
        });
        // Attacca il listener fornito per gestire ulteriori azioni basate sul valore aggiornato
        databaseReference.addValueEventListener(listener);
    }

    public String PersonaggioG2() {
        return personaggioG2;
    }

    public String PersonaggioG1() {
        return personaggioG1;
    }
    public String FormazioneG1() {
        return formazioneG1;
    }
    public String FormazioneG2() {
        return formazioneG2;
    }

    public void ComunicaAttg1(int posizione, ValueEventListener listener)
    {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        databaseReference = instance.getReference(codiceConn);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Se la partita esiste, aggiorna solo il campo PersonaggioGiocatore1
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("azioneg1", posizione);
                    databaseReference.updateChildren(updates);

                } else {
                    // Se la partita non esiste, gestisci l'errore
                    System.err.println("Partita non trovata: " + codiceConn);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gestione dell'errore, se necessario
            }
        });
        // Attacca il listener fornito per gestire ulteriori azioni basate sul valore aggiornato
        databaseReference.addValueEventListener(listener);
    }
    // Listener per rispondere al cambiamento di azioneg1
    public boolean setupAzioneg1Listener() {
        boolean eseguito = false;
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        databaseReference = instance.getReference(codiceConn).child("azioneg1");
        eseguito = true;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int posizione = dataSnapshot.getValue(Integer.class);
                    if(posizione != -1){
                        int rispostag1Value = defence.posizioneColpita(posizione);
                        Map<String, Object> responseUpdate = new HashMap<>();
                        responseUpdate.put("risposta2", rispostag1Value);
                        databaseReference.getParent().updateChildren(responseUpdate);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Errore nel recupero dei dati: " + databaseError.getMessage());
            }

        });
        return eseguito;

    }

    public void ComunicaAttg2(int posizione, ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        databaseReference = instance.getReference(codiceConn);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("azioneg2", posizione);
                    databaseReference.updateChildren(updates);
                } else {
                    // Se la partita non esiste, gestisci l'errore
                    System.err.println("Partita non trovata: " + codiceConn);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Gestione dell'errore, se necessario
            }
        });
        // Attacca il listener fornito per gestire ulteriori azioni basate sul valore aggiornato
        databaseReference.addValueEventListener(listener);
    }

    public boolean setupAzioneg2Listener() {
        boolean eseguito = false;
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        databaseReference = instance.getReference(codiceConn).child("azioneg2");
        eseguito = true;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int posizione = dataSnapshot.getValue(Integer.class);
                    if(posizione != -1){
                        int rispostag1Value = defence.posizioneColpita(posizione);
                        Map<String, Object> responseUpdate = new HashMap<>();
                        responseUpdate.put("risposta1", rispostag1Value);
                        databaseReference.getParent().updateChildren(responseUpdate);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Errore nel recupero dei dati: " + databaseError.getMessage());
            }

        });
        return eseguito;
    }
}
