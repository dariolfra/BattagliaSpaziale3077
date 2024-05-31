package com.example.battagliaspaziale3077;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Attack extends Game implements Serializable{
    private int id_pers, modalita;
    private String nome_giocatore1, nome_giocatore2;
    private ConnectionThread comms;
    private Context context;
    private HashMap<Integer, Drawable> indici_mossaspeciale;
    private HashMap<Integer, Drawable> indici_personaggi;
    private Button btn_attacca, btn_att_speciale, btn_regole;
    private TextView giocatore1, giocatore2;
    private ImageView immagine_pers, img_mossa_speciale;
    private int pos;
    private int selectedPos = -1;
    private boolean multiplayer;
    private static int[] casellaColpita = new int[100];
    private static final int[] arrayFormazioneIA = new int[100];
    private static final int[] id_navi = new int[]{2131165439, 2131165441, 2131165438, 2131165442, 2131165443, 2131165440};
    private Animation scale_down, scale_up;
    private ImageView background;
    private float startX, startY, mossaSpecialeX, mossaSpecialeY;
    private GridAdapterAttacco gridAdapterAttacco;
    private GridAdapter gridAdapter;
    //HashMap per controllare se le navi sono state colpite
    private static HashMap<Integer, List<Integer>> formazioneIA = new HashMap<>();
    private MainActivity mainActivity;
    private float[] initialX, initialY;
    private int[] shipSizes = {0,5,0,3,5,5,4,5,3,3,3};
    private int[] rotationDegrees = {0, 0, 0, 0, 0, 0,0, 0, 0,0,0};
    private HashMap<Integer, List<Integer>> Navi;
    private HashMap<Integer, List<Integer>> NaviColpite = new HashMap<>();
    private HashMap<Integer, List<Integer>> NaviAffondate = new HashMap<>();
    private static boolean SingolaVolta = false;
    private static int attacchi_a_segno = 0;
    private static int attacchi_necessari_att_speciale = 5;
    private static final List<Integer> posizioni_colpite = new ArrayList<Integer>();
    private static int navi_affondate = 0;
    private static List<Integer> id_navi_affondate = new ArrayList<>();
    private boolean mossaSpeciale = false;
    private ArrayList<Integer> posSpeciale;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        posSpeciale = new ArrayList<Integer>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);

        //context per visualizzare toast
        context = this.getApplicationContext();

        //parte di codice che imposta header color = black
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        //aquisizione componeneti da layout
        btn_attacca = (Button) findViewById(R.id.attacco);
        giocatore1 = (TextView) findViewById(R.id.txtNomeG1);
        giocatore2 = (TextView) findViewById(R.id.txtNomeG2);
        immagine_pers = (ImageView) findViewById(R.id.img_pers);
        background = (ImageView) findViewById(R.id.background);
        btn_regole = (Button) findViewById(R.id.button2);

        btn_att_speciale = (Button) findViewById(R.id.btn_attacco_speciale);
        img_mossa_speciale = (ImageView) findViewById(R.id.img_mossa_speciale);

        background.setImageDrawable(getResources().getDrawable(R.drawable.background, context.getTheme()));

        NaviColpite = new HashMap<Integer, List<Integer>>();
        NaviAffondate = new HashMap<Integer, List<Integer>>();

        //parte di codice che recupera i dati dall'activity precedente
        Intent gioco = getIntent();
        Navi = (HashMap<Integer, List<Integer>>) gioco.getSerializableExtra("Navi");
        id_pers = gioco.getIntExtra("personaggio", 1);
        modalita = gioco.getIntExtra("mod", 1);
        comms = (ConnectionThread) gioco.getParcelableExtra("comms");

        if (modalita == 1) {
            nome_giocatore1 = gioco.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore2 = "AI";
            giocatore2.setText(nome_giocatore2);
            multiplayer = false;
            //bisogna scegliere random personaggi IA
            if (!SingolaVolta) {
                mainActivity = new MainActivity();
                formazioneIA.putAll(mainActivity.generateRandomShipPositions(gridAdapter, arrayFormazioneIA)); //l'IA posiziona le navi
                SingolaVolta = true;
            }
        } else {
            nome_giocatore1 = gioco.getStringExtra("nome1");
            giocatore1.setText(nome_giocatore1);
            nome_giocatore2 = gioco.getStringExtra("nome2");
            giocatore2.setText(nome_giocatore2);
            multiplayer = true;
        }
        boolean defence = gioco.getBooleanExtra("defenceOrNot", false);
        if (defence) {
            NaviColpite.putAll((HashMap<Integer, List<Integer>>) gioco.getSerializableExtra("NaviColpite"));
            NaviAffondate.putAll((HashMap<Integer, List<Integer>>) gioco.getSerializableExtra("NaviAffondate"));
        }

        popola_mosse_speciale();
        popola_personaggi();
        immagine_pers.setImageDrawable(indici_personaggi.get(id_pers));

        gridAdapterAttacco = new GridAdapterAttacco(this, casellaColpita);
        GridView gridView = findViewById(R.id.gridView);

        // Inizializza gli array per le posizioni iniziali
        initialX = new float[indici_mossaspeciale.size()];
        initialY = new float[indici_mossaspeciale.size()];

        gridView.setAdapter(gridAdapterAttacco);

        //azione del bottone che invia data la cella selezionata invia un messaggio a avversario
        btn_attacca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    btn_attacca.startAnimation(scale_down);
                    btn_attacca.startAnimation(scale_up);
                    if(multiplayer)
                    {
                        comms.InviaMessaggio(String.valueOf(selectedPos));
                        comms.RiceviRisposta();
                        synchronized (comms){
                            comms.wait(3000);
                        }
                        Attacco(comms.GetMessage());
                    }
                    else
                    {
                        contrallaSeColpita();
                        //thread dorme
                    }
                    if(Controllo_Fine_Gioco_AI()){
                        Intent vittoria = new Intent(Attack.this, Fine_Gioco_Activity.class);
                        vittoria.putExtra("nome", nome_giocatore1);
                        vittoria.putExtra("risultato", true);
                        startActivity(vittoria);
                    }
                    else{
                        //dopo invio messaggio e ricezione risposta si sposta da attacco a difesa
                        Intent defence = new Intent(Attack.this, Defence.class);
                        defence.putExtra("mod", modalita);
                        defence.putExtra("nome1", nome_giocatore1);
                        if(multiplayer)
                        {
                            defence.putExtra("nome2", comms.getName());
                        }
                        else{
                            defence.putExtra("nome2", nome_giocatore2);
                        }
                        defence.putExtra("personaggio", id_pers);
                        defence.putExtra("casellaColpita", casellaColpita);
                        defence.putExtra("Navi", (Serializable) Navi);
                        defence.putExtra("NaviColpite", (Serializable) NaviColpite);
                        defence.putExtra("NaviAffondate", (Serializable) NaviAffondate);
                        defence.putExtra("comms", comms);
                        startActivity(defence);
                    }
                }catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }catch (Exception e){
                    CustomToast.showToast(context, "Casella non selezionata", Toast.LENGTH_SHORT);
                }
            }
        });
        // Utilizza un ViewTreeObserver per memorizzare le posizioni iniziali dopo il layout
        ViewTreeObserver viewTreeObserver = img_mossa_speciale.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() { //salva la posizione iniziale dell'immagine
                // Memorizza le posizioni iniziali delle navi
                for (int i = 0; i < indici_mossaspeciale.size(); i++) {
                    initialX[i] = img_mossa_speciale.getX();
                    initialY[i] = img_mossa_speciale.getY();
                }
                // Rimuovi il listener per evitare che venga chiamato più volte
                img_mossa_speciale.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        context = this.getApplicationContext();

        //animazioni
        scale_down = AnimationUtils.loadAnimation(context, R.anim.scale_down);
        scale_up = AnimationUtils.loadAnimation(context, R.anim.scale_up);

        //azione che colora la cella selezionata nella gridview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //da finire
                pos = position;
                if (canAttack()) {
                    if(mossaSpeciale)
                    {
                        //resetto le condizioni della mossa speciale
                        for (int posSpec : posSpeciale)
                        {
                            casellaColpita[posSpec] = 0;
                        }
                        posSpeciale.clear();
                        attacchi_a_segno += 5;
                        mossaSpeciale = false;

                        //riporto l'imageview alla posizione originale
                        img_mossa_speciale.setX(mossaSpecialeX);
                        img_mossa_speciale.setY(mossaSpecialeY);

                        //resetto la rotazione
                        rotationDegrees[id_pers] = 0;
                        img_mossa_speciale.setRotation(rotationDegrees[id_pers]);

                        //nascondo l'imageview
                        img_mossa_speciale.setVisibility(View.INVISIBLE);
                    }
                    if (selectedPos != -1)
                    {
                        casellaColpita[selectedPos] = 0;
                    }
                    casellaColpita[pos] = R.drawable.selected;
                    selectedPos = pos;
                    pos = -1;
                    gridAdapterAttacco.notifyDataSetChanged();
                } else {
                    CustomToast.showToast(context, "Attacco già sferrato in questo punto", Toast.LENGTH_SHORT);
                }
            }
        });


        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Ruota l'immagine di 90 gradi
                rotationDegrees[id_pers] = (rotationDegrees[id_pers] + 90) % 360;
                img_mossa_speciale.setRotation(rotationDegrees[id_pers]);
                return true;
            }
        });
        img_mossa_speciale.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:

                    float dx = event.getRawX() - startX;
                    float dy = event.getRawY() - startY;

                    img_mossa_speciale.setX(img_mossa_speciale.getX() + dx);
                    img_mossa_speciale.setY(img_mossa_speciale.getY() + dy);

                    startX = event.getRawX();
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    int[] gridLocation = new int[2];
                    gridView.getLocationInWindow(gridLocation);
                    float x = img_mossa_speciale.getX() + img_mossa_speciale.getWidth() / 2;
                    float y = img_mossa_speciale.getY() + img_mossa_speciale.getHeight() / 2;
                    int column = (int) ((x - gridLocation[0]) / (gridView.getWidth() / 10) - 1);
                    int row = (int) ((y - gridLocation[1]) / (gridView.getHeight() / 10) + 1);
                    int position = row * 10 + column;
                    int size = shipSizes[id_pers];
                    int posizione = gridAdapterAttacco.AggiustaPosizioni(id_pers, rotationDegrees[id_pers], position); //per sistemare le posizioni
                    if (ControllaSeOutBound(gridAdapterAttacco.getColumnFromPosition(posizione), size, id_pers, rotationDegrees[id_pers], posizione)) {
                        //inserimento delle navi
                        posizionaAttacco(id_pers, size, rotationDegrees[id_pers], posizione, casellaColpita);
                    }
                    // Resetta la posizione x e y alle posizioni iniziali
                    img_mossa_speciale.setX(initialX[id_pers]);
                    img_mossa_speciale.setY(initialY[id_pers]);
                    gridAdapterAttacco.notifyDataSetChanged();
                    break;
            }

            return true;
        });
    }

    public void confermaAttacco(View v)
    {
        try{
            btn_attacca.startAnimation(scale_down);
            btn_attacca.startAnimation(scale_up);
            if(multiplayer)
            {
                comms.InviaMessaggio(String.valueOf(selectedPos));
                comms.RiceviRisposta();
                synchronized (comms){
                    comms.wait(3000);
                }
                Attacco(comms.GetMessage());
            }
            else
            {
                contrallaSeColpita();
                //thread dorme
            }

            //dopo invio messaggio e ricezione risposta si sposta da attacco a difesa
            Intent defence = new Intent(Attack.this, Defence.class);
            defence.putExtra("mod", modalita);
            defence.putExtra("nome1", nome_giocatore1);
            if(multiplayer)
            {
                defence.putExtra("nome2", comms.getName());
            }
            else{
                defence.putExtra("nome2", nome_giocatore2);
            }
            defence.putExtra("personaggio", id_pers);
            defence.putExtra("casellaColpita", casellaColpita);
            defence.putExtra("Navi", (Serializable) Navi);
            defence.putExtra("NaviColpite", (Serializable) NaviColpite);
            defence.putExtra("NaviAffondate", (Serializable) NaviAffondate);
            defence.putExtra("comms", comms);
            startActivity(defence);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }catch (Exception e){
            CustomToast.showToast(context, "Casella non selezionata", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        customDialog.showDialog(this);
    }

    public void btn_regole_pressed(View v){
        btn_regole.startAnimation(scale_down);
        btn_regole.startAnimation(scale_up);
        regoleDialog.showDialog(this);
    }

    public void Abbandona()
    {
        if(modalita != 1){
            comms.Abbandona();
        }
        Intent HA = new Intent(Attack.this, HomeActivity.class);
        startActivity(HA);
    }

    private void contrallaSeColpita() {
        if(mossaSpeciale)
        {
            for (int posSpec : posSpeciale)
            {
                if(arrayFormazioneIA[posSpec] != 0){
                    CustomToast2.showToast(context, "Bersaglio Colpito!", 5);
                    casellaColpita[posSpec] = R.drawable.nave_colpita;
                    posizioni_colpite.add(posSpec);
                }
                else{
                    CustomToast2.showToast(context, "Acqua!", 5);
                    casellaColpita[posSpec] = R.drawable.naveda1;
                }
            }
        }
        else
        {
            if(arrayFormazioneIA[selectedPos] != 0){
                CustomToast2.showToast(context, "Bersaglio Colpito!", 5);
                casellaColpita[selectedPos] = R.drawable.nave_colpita;
                attacchi_a_segno++;
                if(attacchi_a_segno == 5){
                    CustomToast2.showToast(context, "Attacco Speciale disponibile!", 5);
                }
                posizioni_colpite.add(selectedPos);
            }
            else{
                CustomToast2.showToast(context, "Acqua!", 5);
                casellaColpita[selectedPos] = R.drawable.naveda1;
            }
        }
    }

    public boolean Controllo_Fine_Gioco_AI(){
        boolean risultato = false;
        for(int id : id_navi){
            if(!id_navi_affondate.contains(id)){
                List<Integer> posizioni_nave_IA = formazioneIA.get(id);
                int ship_size = posizioni_nave_IA.size();
                int colpi_a_segno = 0;
                for(int pos : posizioni_colpite){
                    if(posizioni_nave_IA.contains(pos)){
                        colpi_a_segno++;
                    }
                }
                if(colpi_a_segno == ship_size){
                    navi_affondate++;
                    Nave_Affondata(posizioni_nave_IA);
                    id_navi_affondate.add(id);
                    Log.i("NAVI AFFONDATE", String.valueOf(navi_affondate));
                }
            }
        }
        if(navi_affondate == 6){
            risultato = true;
        }
        return risultato;
    }

    public void Nave_Affondata(List<Integer> lista){
        for(Integer i : lista){
            casellaColpita[i] = R.drawable.nave_affondata;
        }
    }
    public boolean canAttack()
    {
        if (casellaColpita[pos] == 0)
        {
            return true;
        }
        else if(casellaColpita[pos] == R.drawable.selected)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void Attacco(String result)
    {
        if(selectedPos != -1)
        {
            CustomToast2.showToast(context, "Bersaglio Colpito!", Toast.LENGTH_SHORT);
            casellaColpita[selectedPos] = 2;
            attacchi_a_segno += 1;
            if(attacchi_a_segno >= 5){
                CustomToast2.showToast(context, "Attacco Speciale disponibile!", Toast.LENGTH_SHORT);
            }
        }
        else if(result == "acqua")
        {
            CustomToast2.showToast(context, "Acqua!", Toast.LENGTH_SHORT);
            casellaColpita[selectedPos] = 1;
        }
        else //esempio stringa: "colpita e affondata|coordinata1-coordinata2-coordinata3..."
        {
            NaveColpitaEAffondata(result.split("|")[1]);
        }
    }

    public void NaveColpitaEAffondata(String pos)
    {
        String[] posizioni = pos.split("-");
        for (String s: posizioni)
        {
            casellaColpita[Integer.valueOf(s)] = 3;
        }
    }

    public void popola_mosse_speciale()
    {
        indici_mossaspeciale = new HashMap<>();
        indici_mossaspeciale.put(1, getResources().getDrawable(R.drawable.attaccoserpente, context.getTheme()));
        indici_mossaspeciale.put(2, getResources().getDrawable(R.drawable.attaccot, context.getTheme())); //ha attacco random
        indici_mossaspeciale.put(3, getResources().getDrawable(R.drawable.attaccocroce, context.getTheme()));
        indici_mossaspeciale.put(4, getResources().getDrawable(R.drawable.attaccotratteggio, context.getTheme()));
        indici_mossaspeciale.put(5, getResources().getDrawable(R.drawable.attaccoscala, context.getTheme()));
        indici_mossaspeciale.put(6, getResources().getDrawable(R.drawable.attaccospada, context.getTheme()));
        indici_mossaspeciale.put(7, getResources().getDrawable(R.drawable.attaccoda5, context.getTheme()));
        indici_mossaspeciale.put(8, getResources().getDrawable(R.drawable.attacox, context.getTheme()));
        indici_mossaspeciale.put(9, getResources().getDrawable(R.drawable.attaccos, context.getTheme()));
        indici_mossaspeciale.put(10, getResources().getDrawable(R.drawable.attaccot, context.getTheme()));
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

    public void genera_img_mossa_speciale(View view) throws InterruptedException {
        btn_att_speciale.startAnimation(scale_down);
        btn_att_speciale.startAnimation(scale_up);;
        //img_mossa_speciale.setImageDrawable(indici_mossaspeciale.get(id_pers));
        if (attacchi_a_segno >= 5) { //se posso fare la mossa speciale
            mossaSpecialeX = img_mossa_speciale.getX();
            mossaSpecialeY = img_mossa_speciale.getY();
            img_mossa_speciale.setVisibility(View.VISIBLE);
            mossaSpeciale = true;
            if (id_pers == 2) { //controllo se sono Giorgia Meloni
                AttaccoRandom(casellaColpita);
            }
            else {
                img_mossa_speciale.setImageDrawable(indici_mossaspeciale.get(id_pers));
            }
            attacchi_a_segno -= 5;
        }
        else { //se non è ancora tempo della mossa speciale
            CustomToast.showToast(this,"Attacco Speciale disponibile tra " + (attacchi_necessari_att_speciale - attacchi_a_segno) + " attacchi",Toast.LENGTH_LONG);
        }
    }


    private void AttaccoRandom(int[] immaginiCaselle) { //attacco della meloni
        if(casellaColpita[selectedPos] == R.drawable.selected)
        {
            casellaColpita[selectedPos] = 0;
        }
        for (int i = 0; i < 7; i ++){
            Random random = new Random();
            int p = random.nextInt(100); //numero da 0 a 99
            ImmaginiNavi(p,immaginiCaselle);
        }
        gridAdapterAttacco.notifyDataSetChanged();
    }

    //inserisce l'immagine nella casella indicata nel gridview
    public void ImmaginiNavi(int position,int[] immaginiCasella) {
        if(casellaColpita[position] == 0)
        {
            posSpeciale.add(position);
            immaginiCasella[position] = R.drawable.selected;
            gridAdapterAttacco.notifyDataSetChanged();
        }
    }
    public boolean ControllaSeOutBound(int column, int size, int index, int rotation, int position) {
        //verifica che l'attacco sia correttamente dentro il gridview
        if(position < 0 || position > 99){ //99 == immaginiCasella.lenght
            return false;
        }
        else if(index == 3 && (position > 9 && position < 90) && (column != 0 && column < 9) ||
                index == 7 && (rotation == 0 || rotation == 180) && column < 6 ||
                index == 7 && (rotation == 90 || rotation == 270) && position < 60 ||
                index == 9 && (rotation == 0 || rotation == 180) && position < 80 && (column != 0 && column != 9) ||
                index == 9 && (rotation == 90 || rotation == 270) && column < 8 && (position > 9 && position < 90 ) ||
                index == 5 && (rotation == 0 || rotation == 180) && column < 6 && position < 69 ||
                index == 5 && (rotation == 90 || rotation == 270) && column < 7 && position > 39 ||
                index == 1 && rotation == 0 && position > 9 && column < 6  ||
                index == 1 && rotation == 90 && column < 9 && position < 60 ||
                index == 1 && rotation == 180 && position < 90 && column < 6 ||
                index == 1 && rotation == 270 && column > 0 && position < 60 ||
                index == 6 && (rotation == 0 || rotation == 180) && column < 8 && (position > 9 && position < 90) ||
                index == 6 && (rotation == 90 || rotation == 270 ) && (column < 9 && column > 0) && position < 80 ||
                index == 10 && (rotation == 0 || rotation == 180) && column < 8 &&(position > 9 && position < 90) ||
                index == 10 && rotation == 90 && (column > 0 && column < 9) && (position < 80 && position > 9) ||
                index == 10 && rotation == 270 && (column > 0 && column < 9) && position > 19 ||
                index == 4 && (rotation == 0 || rotation == 180) && (column < 3) ||
                index == 4 && ((rotation == 90 || rotation == 270) && position < 19) ||
                index == 8 && position < 80 && column < 8){
            return true;
        }
        else {
            return false;
        }
    }
    private void posizionaAttacco(int index, int size, int rotationDegrees, int posizione,int[] immaginiCasella) {
        //insercisce l'attacco all'interno della griglia

        if(selectedPos != -1)
        {
            casellaColpita[selectedPos] = 0;
        }

        for (int j = 0; j < size; j++) {
            int currentPos = posizione + j;
            int p;

            switch (index) {
                case 3:
                case 7:
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        ImmaginiNavi(currentPos,immaginiCasella);
                        if (index == 3 && j == 1){
                            p = posizione - 10 + j;
                            ImmaginiNavi(p,immaginiCasella);
                        }
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        ImmaginiNavi(posizione + j * 10,immaginiCasella);
                        if (index == 3 && j == 1) {
                            p = posizione + j * 10 + 1;
                            ImmaginiNavi(p,immaginiCasella);
                        }
                    }
                    break;

                case 9:
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        ImmaginiNavi(posizione + j * 10 ,immaginiCasella);
                        if(j == 0){
                            ImmaginiNavi(posizione + 1,immaginiCasella);
                        }
                        if(j == 2){
                            ImmaginiNavi(posizione + j * 10 - 1,immaginiCasella);
                        }
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        ImmaginiNavi(currentPos,immaginiCasella);
                        if(j == 0){
                            ImmaginiNavi(posizione - 10 ,immaginiCasella);
                        }
                        if(j == 2){
                            ImmaginiNavi(posizione + j + 10,immaginiCasella);
                        }
                    }
                    break;
                case 5:
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        ImmaginiNavi(posizione + 10 * j + j,immaginiCasella);
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        ImmaginiNavi(posizione - j * 10 + j,immaginiCasella);
                    }
                    break;
                case 1:
                    if(rotationDegrees == 0){
                        if(j == 0 || j == 2 || j == 4)
                        {
                            ImmaginiNavi(posizione + j,immaginiCasella);
                        }
                        else{
                            ImmaginiNavi(posizione + j - 10 ,immaginiCasella);
                        }
                    }
                    else if (rotationDegrees == 90){
                        if(j == 0 || j == 2 || j == 4){
                            ImmaginiNavi(posizione + 10 * j,immaginiCasella);
                        }else {
                            ImmaginiNavi(posizione + 10 * j + 1,immaginiCasella);
                        }
                    }
                    else if(rotationDegrees == 180){
                        if(j == 0 || j == 2 || j == 4){
                            ImmaginiNavi(posizione + j,immaginiCasella);
                        }
                        else {
                            ImmaginiNavi(posizione + 10 + j,immaginiCasella);
                        }
                    } else if (rotationDegrees == 270) {
                        if(j == 0 || j == 2 || j == 4){
                            ImmaginiNavi(posizione + 10 * j,immaginiCasella);
                        }
                        else {
                            ImmaginiNavi(posizione + 10 * j - 1,immaginiCasella);
                        }
                    }
                    break;
                case 6:
                    if(rotationDegrees == 0 || rotationDegrees == 180){
                        if(j != 1 && rotationDegrees == 0 || j != 2 && rotationDegrees == 180){
                            ImmaginiNavi(posizione + j,immaginiCasella);
                        }else {
                            ImmaginiNavi(posizione + j - 10,immaginiCasella);
                            ImmaginiNavi(posizione + j + 10,immaginiCasella);
                        }
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        if(j != 1 && rotationDegrees == 90 || j != 2 && rotationDegrees == 270){
                            ImmaginiNavi(posizione + 10 * j,immaginiCasella);
                        }else {
                            ImmaginiNavi(posizione + j * 10 + 1,immaginiCasella);
                            ImmaginiNavi(posizione + j * 10 - 1,immaginiCasella);
                        }
                    }
                    break;
                case 10:
                    ImmaginiNavi(currentPos,immaginiCasella);
                    if(j == 1 && rotationDegrees == 90 ){
                        ImmaginiNavi(posizione + 10 + j,immaginiCasella);
                        ImmaginiNavi(posizione + 20 + j,immaginiCasella);
                    }
                    else if(j == 2 && rotationDegrees == 180 || j == 0 && rotationDegrees == 0){
                        ImmaginiNavi(posizione - 10 + j,immaginiCasella);
                        ImmaginiNavi(posizione + 10 + j,immaginiCasella);
                    } else if (j == 1 && rotationDegrees == 270) {
                        ImmaginiNavi(posizione - 10 + j,immaginiCasella);
                        ImmaginiNavi(posizione - 20 + j,immaginiCasella);
                    }
                    break;
                case 4:
                    if(rotationDegrees == 0 || rotationDegrees == 180){
                        ImmaginiNavi(posizione + 2 * j,immaginiCasella);
                    } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                        ImmaginiNavi(posizione + 10 * 2 * j,immaginiCasella);
                    }
                    break;
                case 8:
                    if(j == 0 || j == 2){
                        ImmaginiNavi(posizione + j,immaginiCasella);
                        ImmaginiNavi(posizione + j + 20,immaginiCasella);
                    }
                    else if(j == 1){
                        ImmaginiNavi(posizione + 10 + j,immaginiCasella);
                    }
                    break;
                default:
                    break;
            }

            // Additional checks for specific rotations
            if (rotationDegrees == 180 || rotationDegrees == 0) {
                if (index == 3 && j == 1){
                    p = posizione + 10 + j;
                    ImmaginiNavi(p,immaginiCasella);
                }
            } else if (rotationDegrees == 90 || rotationDegrees == 270) {
                if (index == 3 && j == 1) {
                    p = posizione + j * 10 - 1;
                    ImmaginiNavi(p,immaginiCasella);
                }
            }
        }
        img_mossa_speciale.setVisibility(View.INVISIBLE);
    }
}
