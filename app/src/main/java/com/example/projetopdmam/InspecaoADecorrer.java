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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Caso;
import com.example.projetopdmam.Modelos.Inspecao;
import com.example.projetopdmam.Modelos.Obra;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InspecaoADecorrer extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    Utilizador loggedInUser;
    Inspecao inspecaoADecorrer;
    Obra obra;
    List<Caso> listaCasos;

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_inspecao_adecorrer);

        loggedInUser = bd.getLoggedInUser();
        inspecaoADecorrer = bd.getInspecaoADecorrer();
        obra = bd.getObraPorId(inspecaoADecorrer.getObraId());
        listaCasos = bd.getCasosPorIdInspecao(inspecaoADecorrer.getId());

        listview = findViewById(R.id.listview);



        ListAdapter listAdapter = new ListAdapter(InspecaoADecorrer.this, listaCasos);
        listview.setAdapter(listAdapter);
        listview.setClickable(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetalhesCaso.class);
                intent.putExtra("CasoId", listaCasos.get(i).getId());
                startActivity(intent);
            }
        });

        if(isInternetAvailable()){
            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getInspecaoAtivaPorIdInspetorIdObra(loggedInUser.getId(), inspecaoADecorrer.getObraId());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.body().get("Success").getAsBoolean()){

                    }else{
                        bd.acabarInspecaoLocal();
                        Toast.makeText(getApplicationContext(), "A inspeção que estava guardarda localmente já foi finalizada!", Toast.LENGTH_LONG).show();
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
        //Button btn_TerminarInspecao = findViewById(R.id.btn_TerminarInspecao);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("Tem a certeza que quer cancelar a inspeção atual? Ao cancelar a inspeção atual serão peridas todas as informações dos casos!",
                    new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isInternetAvailable()){
                            Call<JsonObject> call1 = RetrofitClient.getInstance().getMyApi().cancelarInspecao(inspecaoADecorrer.getId());
                            call1.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.body().get("Success").getAsBoolean()){
                                        bd.acabarInspecaoLocal();
                                        Toast.makeText(getApplicationContext(), "Inspeção cancelada com sucesso", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Aconteceu algo errado por parte do servidor", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "É necessária uma conexão à internet para efetuar essa operação!", Toast.LENGTH_LONG).show();
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

        /*btn_TerminarInspecao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("Tem a certeza que quer finalizar a inspeção? (A inspeção será guardada e nenhum dado será perdido)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isInternetAvailable()){
                            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().terminarInspecao(inspecaoADecorrer.getId());
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.body().get("Success").getAsBoolean()){
                                        bd.acabarInspecaoLocal();
                                        Toast.makeText(getApplicationContext(), "A inspeção foi finalizada com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Aconteceu algo errado ao tentar finalizar a inspeção", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "É necessária uma conexão à internet para efetuar essa operação!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, null);
            }
        });*/

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
        new AlertDialog.Builder(InspecaoADecorrer.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", cancelListener)
                .create()
                .show();
    }

}