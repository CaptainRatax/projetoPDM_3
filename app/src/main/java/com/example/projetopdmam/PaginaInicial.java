package com.example.projetopdmam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Estacionamento;
import com.example.projetopdmam.Modelos.Lugar;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginaInicial extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    private static final int PERMISSION_REQUEST_CODE = 200;

    Utilizador loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loggedInUser = bd.getLoggedInUser();

        Estacionamento estacionamentoADecorrer = bd.getEstacionamentoADecorrer();

        if(estacionamentoADecorrer.isActive()){
            Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
            startActivity(intent);
        }else{
            if(isInternetAvailable()){
                Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getEstacionamentoAtivoPorIdUtilizador(loggedInUser.getId());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.body().get("Sucesso").getAsBoolean()){
                            JsonObject lugarJson = response.body().get("Lugar").getAsJsonObject();
                            //Cria um objeto do tipo Lugar usando o Json que recebeu da API
                            Lugar lugar = new Lugar();
                            lugar.setId(lugarJson.get("Id").getAsInt());
                            lugar.setCodigo(lugarJson.get("Codigo").getAsString());
                            lugar.setAndar(lugarJson.get("Andar").getAsString());
                            lugar.setActive(true);

                            JsonObject estacionamento = response.body().get("Estacionamento").getAsJsonObject();
                            //Cria um objeto do tipo Estacionamento usando o Json que recebeu da API
                            estacionamentoADecorrer.setId(estacionamento.get("Id").getAsInt());
                            estacionamentoADecorrer.setUtilizadorId(estacionamento.get("UtilizadorId").getAsInt());
                            estacionamentoADecorrer.setLugarId(estacionamento.get("LugarId").getAsInt());
                            estacionamentoADecorrer.setDataEntrada(estacionamento.get("DataEntrada").getAsString());
                            estacionamentoADecorrer.setDataSaida(estacionamento.get("DataSaida").getAsString());
                            estacionamentoADecorrer.setEstacionamentoLivre(estacionamento.get("EstacionamentoLivre").getAsBoolean());
                            estacionamentoADecorrer.setActive(true);

                            if (bd.getEstacionamentoADecorrer().isActive()) { //Verifica se existe algum estacionamento a decorrer localmente
                                //Existe um estacionamento a decorrer localmente
                                if (bd.getEstacionamentoADecorrer() != estacionamentoADecorrer) { //Verifica se o estacionamento que está a decorrer localmente é diferente do recebido
                                    bd.acabarEstacionamentoLocal(); //Se for acaba o estacionamento a decorrer localmente
                                    bd.comecarEstacionamentoLocal(estacionamentoADecorrer); //e começa um novo com os dados do estacionamento recebidos da API
                                }
                            } else {
                                //Não existe nenhum estacionamento a decorrer localmente
                                bd.comecarEstacionamentoLocal(estacionamentoADecorrer); //Começa o estacionamento localmente
                            }
                            if (bd.getLugarLocal().isActive()) {//Verifica se o lugar já existe localmente
                                //O lugar existe localmente
                                if (bd.getLugarLocal() != lugar) {//Verifica se o lugar que existe localmente é diferente do recebido da API
                                    bd.editarLugar(lugar); //Se for altera o lugar local e coloca os dados do lugar recebido da API
                                }
                            } else {
                                //O lugar não existe localmente
                                bd.adicionarLugar(lugar); //Cria o lugar localmente
                            }
                            Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }
        }

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_pagina_inicial);

        TextView txt_BemVindo = findViewById(R.id.txt_BemVindo);
        txt_BemVindo.setText("Bem vindo " + loggedInUser.getNome() + "!");

        FloatingActionButton btn_Logout = findViewById(R.id.btn_Logout);
        Button btn_QRCodeScanner = findViewById(R.id.btn_QRCodeScanner);
        ImageView imageView = findViewById(R.id.imageView);

        btn_QRCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    if(isInternetAvailable()){
                        Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(PaginaInicial.this, "É necessário uma conexão à internet...", Toast.LENGTH_LONG).show();
                    }
                }else{
                    requestPermission();
                    Toast.makeText(PaginaInicial.this, "É necessário aceitar a permissão de acesso à câmara!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    if(isInternetAvailable()){
                        Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(PaginaInicial.this, "É necessário uma conexão à internet...", Toast.LENGTH_LONG).show();
                    }
                }else{
                    requestPermission();
                    Toast.makeText(PaginaInicial.this, "É necessário aceitar a permissão de acesso à câmara!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageSimNao("Tem a certeza que quer fazer logout?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bd.logoutLocal();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    }
                }, null);
            }
        });
    }

    //Funções auxiliares

    private boolean isInternetAvailable(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PaginaInicial.this)
                .setMessage(message)
                .setPositiveButton("Aceitar", okListener)
                .setNegativeButton("Negar", null)
                .create()
                .show();
    }

    private void showMessageSimNao(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(PaginaInicial.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Nao", cancelListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplicationContext(), QRCodeReader.class);
                    startActivity(intent);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("É necessário aceitar a permissão de acesso à câmara!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }
}