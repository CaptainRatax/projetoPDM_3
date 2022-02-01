package com.example.projetopdmsam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmsam.Backend.BaseDados;
import com.example.projetopdmsam.Backend.RetrofitClient;
import com.example.projetopdmsam.Modelos.Caso;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalhesCaso extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    Caso caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_caso);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Bundle extras = getIntent().getExtras();
        int CasoId = extras.getInt("CasoId");
        caso=bd.getCasoPorId(CasoId);

        FloatingActionButton btn_VoltarDetalhesCaso = findViewById(R.id.btn_VoltarDetalhesCaso);
        TextView txt_Titulo = findViewById(R.id.txt_Titulo);
        TextView txt_Descricao = findViewById(R.id.txt_Descricao);
        ImageView img_CasoDetalhes = findViewById(R.id.img_CasoDetalhes);
        Button btn_EliminarCaso = findViewById(R.id.btn_EliminarCaso);

        txt_Titulo.setText(caso.getTitulo());
        txt_Descricao.setText(caso.getDescricao());
        if(!caso.getImagem().equals("")){
            byte[] decodedByte = Base64.decode(caso.getImagem(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            img_CasoDetalhes.setImageBitmap(bitmap);
        }

        btn_VoltarDetalhesCaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
                startActivity(intent);
            }
        });

        btn_EliminarCaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternetAvailable()){
                    Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().eliminarCaso(caso.getId());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if(response.body().get("Success").getAsBoolean()){
                                bd.eliminarCaso(caso.getId());
                                Toast.makeText(getApplicationContext(), "Caso eliminado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Aconteceu algo errado ao tentar eliminar o caso", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "É necessária uma conexão à internet para efetuar essa operação!", Toast.LENGTH_SHORT).show();
                }
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

}