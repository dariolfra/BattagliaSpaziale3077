package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.battagliaspaziale3077.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    //Effettura controllo della modalita che viene passata dalla pagina che crea il gioco
    ActivityMainBinding binding;
    ImageView[] navi;
    float startX, startY;
    int[] shipSizes = {1, 2, 3, 4}; // Dimensioni delle navi
    Boolean dati_arrivati_correttamente = false;
    Context context;
    String nome_giocatore;
    int modalita;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int[] immaginiCasella = new int[100];
        GridAdapter gridAdapter = new GridAdapter(this, immaginiCasella);

        navi = new ImageView[4];
        navi[0] = findViewById(R.id.naveda1);
        navi[1] = findViewById(R.id.naveda2);
        navi[2] = findViewById(R.id.naveda3);
        navi[3] = findViewById(R.id.naveda4);

        context = this.getApplicationContext();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Intent gioco = getIntent();
                    nome_giocatore = gioco.getStringExtra("nome");
                    modalita = gioco.getIntExtra("mod", 1);
                    dati_arrivati_correttamente = true;
                }catch (Exception e){
                    Toast.makeText(context, "DATI NON PASSATI CORRETTAMENTE", Toast.LENGTH_LONG).show();
                }
                if(dati_arrivati_correttamente){
                    Toast.makeText(context, "Giocatore : " + nome_giocatore + " Modalità : " + modalita , Toast.LENGTH_SHORT).show();
                }
            }
        });

        for (int i = 0; i < navi.length; i++) {
            final int index = i;
            navi[i].setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - startX;
                        float dy = event.getY() - startY;
                        navi[index].setX(navi[index].getX() + dx);
                        navi[index].setY(navi[index].getY() + dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        int[] gridLocation = new int[2];
                        binding.gridView.getLocationInWindow(gridLocation);
                        float x = navi[index].getX() + navi[index].getWidth() / 2;
                        float y = navi[index].getY() + navi[index].getHeight() / 2;
                        int column = (int) ((x - gridLocation[0]) / (binding.gridView.getWidth() / 10) - 1);
                        int row = (int) ((y - gridLocation[1]) / (binding.gridView.getHeight() / 10) + 1);
                        int position = row * 10 + column;
                        int size = shipSizes[index];
                        if (column + size <= 10 && gridAdapter.ControllaSeLiberi(position, size)) {
                            for (int j = 0; j < size; j++) {
                                immaginiCasella[position + j] = R.drawable.naveda1;
                            }
                            Toast.makeText(this, "Posizione della nave " + position + " - " + (position + size - 1), Toast.LENGTH_SHORT).show();
                            navi[index].setVisibility(View.INVISIBLE);
                        } else {

                            Toast.makeText(this, "La nave non può essere posizionata qui " + position, Toast.LENGTH_SHORT).show();
                            navi[index].setX(startX); // Resetta la posizione x
                            navi[index].setY(startY);  // Resetta la posizione y
                            //NON RITORNANO AL POSTO ORIGINALE
                        }
                        gridAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            });
        }
        binding.gridView.setAdapter(gridAdapter);
    }
}
