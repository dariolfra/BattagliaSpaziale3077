package com.example.battagliaspaziale3077;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class User_vs_Ai_Activity extends AppCompatActivity {

    Button btn_gioca;
    TextInputEditText txt_nome;
    String nome_giocatore;
    Boolean nome_corretto;

    int modalita = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_vs_ai);

        Window window = this.getWindow();
        Context context = this.getApplicationContext();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(window.getContext(), R.color.black));

        btn_gioca = (Button) findViewById(R.id.btn_gioca);
        txt_nome = (TextInputEditText) findViewById(R.id.txt_nome);
        btn_gioca.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                nome_corretto = false;
                try{
                    nome_giocatore = String.valueOf(txt_nome.getText());
                    if(nome_giocatore.isEmpty()){
                        throw new Exception();
                    }
                    else{
                        nome_corretto = true;
                    }
                }catch (Exception e){
                    System.out.println(e.toString());
                    Toast toast = Toast.makeText(context, "Nome Giocatore inserito non valido", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    if (view != null) {
                        view.setBackgroundResource(R.drawable.bordo_rettangolo_label);
                        TextView text = (TextView) view.findViewById(android.R.id.message);
                        text.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                    toast.show();
                    nome_corretto = false;
                }
                if(nome_corretto){
                    txt_nome.setText("");
                    Intent gioco = new Intent(User_vs_Ai_Activity.this, PersonaggiActivity.class);
                    gioco.putExtra("mod", modalita);
                    gioco.putExtra("nome", nome_giocatore);
                    startActivity(gioco);
                }
            }
        });
    }
}