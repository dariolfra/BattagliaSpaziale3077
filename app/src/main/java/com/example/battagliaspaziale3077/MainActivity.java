package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.battagliaspaziale3077.databinding.ActivityMainBinding;
import java.io.Serializable;
import java.util.zip.Inflater;
public class MainActivity extends AppCompatActivity {
    //Effettura controllo della modalita che viene passata dalla pagina che crea il gioco

    //Variabili per il gioco
    ActivityMainBinding binding;
    ImageView[] navi;
    float startX, startY;
    float[] initialX, initialY;
    Button btnConferma;
    int[] shipSizes = {3, 2, 2, 4,4,3}; // Dimensioni delle navi
    int[] rotationDegrees = {0, 0, 0, 0, 0, 0}; // Gradi di rotazione delle navi
    boolean[] shipPlaced = {false, false, false, false, false, false}; // Stato delle navi
    private ConnectionThread comms;
    Boolean dati_arrivati_correttamente = false;
    Context context;
    String nome_giocatore1, nome_giocatore2;
    int modalita;
    int personaggio;
    ImageView background;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        int[] immaginiCasella = new int[100];

        GridAdapter gridAdapter = new GridAdapter(this, immaginiCasella);

        background = (ImageView) findViewById(R.id.imageView8);
        background.setImageDrawable(getResources().getDrawable(R.drawable.background, getTheme()));

        //Inizializzazione delle navi
        navi = new ImageView[6];
        navi[0] = findViewById(R.id.navecap);
        navi[1] = findViewById(R.id.naveda2);
        navi[2] = findViewById(R.id.naveangolo);
        navi[3] = findViewById(R.id.naveda4);
        navi[4] = findViewById(R.id.navedaottorino);
        navi[5] = findViewById(R.id.navel);

        btnConferma = findViewById(R.id.btnConferma);
        btnConferma.setVisibility(View.GONE); // Stato Iniziale nascosto

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        //Metodo per confermare e per far partire il gioco
        btnConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attacco = new Intent(MainActivity.this, Attack.class);
                attacco.putExtra("mod", modalita);
                attacco.putExtra("nome1", nome_giocatore1);
                attacco.putExtra("nome2", nome_giocatore2);
                attacco.putExtra("personaggio", personaggio);
                //passare anche posizioni delle navi così comunicarlo anche all'avversario se colpisce una nave alleata
                attacco.putExtra("comms", (Serializable) comms);
                startActivity(attacco);
            }
        });

        // Inizializza gli array per le posizioni iniziali
        initialX = new float[navi.length];
        initialY = new float[navi.length];

        context = this.getApplicationContext();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Intent personaggi = getIntent();
                    modalita = personaggi.getIntExtra("mod", 1);
                    if(modalita == 1){
                        nome_giocatore1 = personaggi.getStringExtra("nome1");
                    }
                    else{
                        nome_giocatore1 = personaggi.getStringExtra("nome1");
                        nome_giocatore2 = personaggi.getStringExtra("nome2");
                    }
                    personaggio = personaggi.getIntExtra("personaggio", 1);
                    comms = (ConnectionThread) personaggi.getSerializableExtra("comms");
                    dati_arrivati_correttamente = true;
                }catch (Exception e){
                    CustomToast.showToast(context, "Dati non passati correttamente", Toast.LENGTH_LONG);
                }
                if(dati_arrivati_correttamente){
                    CustomToast.showToast(context, "Giocatore: " + nome_giocatore1 + " / Modalità: " + modalita + " / Personaggio: " +personaggio, Toast.LENGTH_SHORT);
                }
            }
        });

        binding.gridView.setAdapter(gridAdapter);
        // Utilizza un ViewTreeObserver per memorizzare le posizioni iniziali dopo il layout
        ViewTreeObserver viewTreeObserver = binding.getRoot().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Memorizza le posizioni iniziali delle navi
                for (int i = 0; i < navi.length; i++) {
                    initialX[i] = navi[i].getX();
                    initialY[i] = navi[i].getY();
                }
                // Rimuovi il listener per evitare che venga chiamato più volte
                binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        for (int i = 0; i < navi.length; i++) {
            final int index = i;

            GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    // Ruota l'immagine di 90 gradi
                    rotationDegrees[index] = (rotationDegrees[index] + 90) % 360;
                    navi[index].setRotation(rotationDegrees[index]);
                    return true;
                }
            });
            navi[i].setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - startX;
                        float dy = event.getRawY() - startY;

                        navi[index].setX(navi[index].getX() + dx);
                        navi[index].setY(navi[index].getY() + dy);

                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        int[] gridLocation = new int[2];
                        binding.gridView.getLocationInWindow(gridLocation);
                        float x = navi[index].getX() + (float) navi[index].getWidth() / 2;
                        float y = navi[index].getY() + (float) navi[index].getHeight() / 2;
                        int column = (int) ((x - gridLocation[0]) / (binding.gridView.getWidth() / 10) - 1);
                        int row = (int) ((y - gridLocation[1]) / (binding.gridView.getHeight() / 10) + 1);
                        int position = row * 10 + column;
                        int size = shipSizes[index];
                        int posizione = gridAdapter.AggiustaPosizioni(index,rotationDegrees[index],position); //per sistemare le posizioni
                        if (ControllaSeOutBound(gridAdapter.getColumnFromPosition(posizione),size,index,rotationDegrees[index],posizione) && gridAdapter.ControllaSeLiberi(posizione, size,index,rotationDegrees[index])) {
                            //inserimento delle navi
                            posizionaNave(index,size,rotationDegrees[index],posizione,immaginiCasella);
                        }
                        // Resetta la posizione x e y alle posizioni iniziali
                        navi[index].setX(initialX[index]);
                        navi[index].setY(initialY[index]);
                        gridAdapter.notifyDataSetChanged();

                        // Controlla se tutte le navi sono state inserite
                        if (allShipsPlaced()) {
                            btnConferma.setVisibility(View.VISIBLE);
                        }

                        break;
                }
                return true;
            });
        }
    }
    public boolean ControllaSeOutBound(int column, int size, int index, int rotation, int position) {

        // Controllo delle posizioni fuori dai limiti per rotazioni specifiche
        if (index == 0 && rotation == 0 && position < 10 ||
                (index == 0 && rotation == 270 && (column <= 0 || position > 79)) ||
                (index == 0 && rotation == 90 && position > 79) ||
                (index == 1 && (rotation == 90 || rotation == 180) && position > 89) ||
                index == 2 && rotation == 0 && position < 10 ||
                (index == 2 && ((rotation == 90 && (position - 8) % 10 == 0) ||
                        (rotation == 180 && position > 89) ||
                        (rotation == 270 && position < 10))) ||
                (index == 3 && (rotation == 90 || rotation == 270) && position > 69) ||
                (index == 3 && (rotation == 0 || rotation == 180) && column > 7) ||
                (index == 4 && rotation == 0 && position < 10) ||
                (index == 4 && rotation == 90 && position > 69) ||
                (index == 4 && rotation == 270 && column == 0) ||
                (index == 5 && rotation == 0 && position < 10) ||
                (index == 5 && rotation == 270 && column == 0) ||
                (index == 5 && ((rotation == 90 || rotation == 270) && position > 79))) {
            return false;
        }
        // Controlli per rotazioni specifiche e colonne
        if (    (index == 0 && rotation == 90 && column < 9 ) ||
                (index == 0 && rotation == 270 && column > 0 ) ||
                (index == 3 && (rotation == 90 || rotation == 270) && position >= 9) ||
                (index == 1 && (rotation == 90 || rotation == 270)) && position < 90 ||
                (index == 4 && ((rotation == 90 && position > 19 && position <= 89) ||
                        (rotation == 180 && position < 90 && column < 7) ||
                        (rotation == 270 && column > 0 && position < 69)))  ||
                (index == 5 && ((rotation == 90 || rotation == 270) && position < 79))) {
            return true;
        }
        // Controllo generale delle colonne
        return column + size <= 10;
    }
    private void posizionaNave(int index, int size, int rotationDegrees, int posizione,int[] immaginiCasella) {
        for (int j = 0; j < size; j++) {
            int currentPos = posizione + j;
            int p;

            switch (index) {
                case 0:
                case 1:
                case 2:
                case 4:
                case 5:
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        ImmaginiNavi(currentPos,index,immaginiCasella);
                        if ((index == 2 && j == 0 && rotationDegrees == 0) || (index == 0 && j == 1 && rotationDegrees == 0) || (index == 4 && j == 2 && rotationDegrees == 0) || (index == 5 && j == 2 && rotationDegrees == 0)) {
                            p = posizione - 10 + j;
                            ImmaginiNavi(p,index,immaginiCasella);
                        }
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        if(index == 2 && rotationDegrees == 270){
                            ImmaginiNavi(currentPos,index,immaginiCasella);
                        }
                        else {
                            ImmaginiNavi(posizione + j * 10,index,immaginiCasella);
                        }
                        if ((index == 0 && j == 1 && rotationDegrees == 90) || (index == 2 && j == 0 && rotationDegrees == 90) || (index == 5 && j == 2 && rotationDegrees == 90) || (index == 4 && j == 2 && rotationDegrees == 90)) {
                            p = posizione + j * 10 + 1;
                            ImmaginiNavi(p,index,immaginiCasella);
                        }
                    }
                    break;

                case 3:
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        ImmaginiNavi(currentPos,index,immaginiCasella);
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        ImmaginiNavi(posizione + j * 10,index,immaginiCasella);
                    }
                    break;

                default:
                    break;
            }

            // Additional checks for specific rotations
            if (rotationDegrees == 180) {
                if ((index == 2 && j == 1) || (index == 0 && j == 1) || (index == 5 && j == 0) || (index == 4 && j == 1)) {
                    p = posizione + 10 + j;
                    ImmaginiNavi(p,index,immaginiCasella);
                }
            } else if (rotationDegrees == 270) {
                if ((index == 4 && j == 1) || (index == 0 && j == 1) || (index == 5 && j == 0)) {
                    p = posizione + j * 10 - 1;
                    ImmaginiNavi(p,index,immaginiCasella);
                } else if (index == 2 && j == 1) {
                    p = posizione - 10 + 1;
                    ImmaginiNavi(p,index,immaginiCasella);
                }
            }
        }
        navi[index].setVisibility(View.INVISIBLE);
        shipPlaced[index] = true; // Segna la nave inserita
    }

    public void ImmaginiNavi(int position,int index,int[] immaginiCasella){
        //Ogni nave ha la sua immagine
        if (index == 0) {
            immaginiCasella[position] = R.drawable.navexcap;
        } else if (index == 1) {
            immaginiCasella[position] = R.drawable.navexnave2;
        } else if (index == 2) {
            immaginiCasella[position] = R.drawable.navexangolo;
        } else if (index == 3) {
            immaginiCasella[position] = R.drawable.navexnave4;
        } else if (index == 4) {
            immaginiCasella[position] = R.drawable.navexottorino;
        } else if (index == 5) {
            immaginiCasella[position] = R.drawable.navexelle;
        }
    }
    private boolean allShipsPlaced() { //controlla se tutte le navi sono state inserite
        for (boolean placed : shipPlaced) {
            if (!placed) {
                return false;
            }
        }
        return true;
    }


}
