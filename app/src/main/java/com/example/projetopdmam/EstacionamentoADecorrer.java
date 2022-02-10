package com.example.projetopdmam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Caso;
import com.example.projetopdmam.Modelos.Estacionamento;
import com.example.projetopdmam.Modelos.Lugar;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstacionamentoADecorrer extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    Utilizador loggedInUser;
    Estacionamento estacionamentoADecorrer;
    Lugar lugar;
    Caso caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_estacionamento_adecorrer);

        loggedInUser = bd.getLoggedInUser();
        estacionamentoADecorrer = bd.getEstacionamentoADecorrer();
        lugar = bd.getLugarLocal();
        caso = bd.getCasoPorIdEstacionamento(estacionamentoADecorrer.getId());

        if(isInternetAvailable()){
            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getEstacionamentoAtivoPorIdUtilizadorIdLugar(loggedInUser.getId(), estacionamentoADecorrer.getLugarId());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.body().get("Sucesso").getAsBoolean()){

                    }else{
                        bd.acabarEstacionamentoLocal();
                        Toast.makeText(getApplicationContext(), "O estacionamento que estava guardardo localmente já terminou!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Sem conexão à internet!", Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton btn_cancel = findViewById(R.id.btn_cancel);
        FloatingActionButton btn_NovoCaso = findViewById(R.id.btn_NovoCaso);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("Tem a certeza que quer terminar o estacionamento atual?",
                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isInternetAvailable()){
                            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().saida(loggedInUser.getId());
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.body().get("Sucesso").getAsBoolean()){
                                        bd.acabarEstacionamentoLocal();
                                        Toast.makeText(getApplicationContext(), "O estacionamento foi terminado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Aconteceu algo errado ao tentar finalizar o estacionamento", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "É necessária uma conexão à internet para efetuar essa operação!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                    null);
            }
        });

        btn_NovoCaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NovoCaso.class);
                startActivity(intent);
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(EstacionamentoADecorrer.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", cancelListener)
                .create()
                .show();
    }

}