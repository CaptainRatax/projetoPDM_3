package com.example.projetopdmam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Caso;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarCaso extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    BaseDados bd = new BaseDados(this);

    private DrawerLayout drawer;

    Utilizador loggedInUser;
    Caso caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_caso);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        loggedInUser=bd.getLoggedInUser();

        Toolbar toolbar = findViewById(R.id.toolbar_editar_caso);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_editar_caso);
        NavigationView navigationView = findViewById(R.id.nav_view_editar_caso);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        View headerView = navigationView.getHeaderView(0);
        TextView txt_Nome = (TextView) headerView.findViewById(R.id.nav_nome);
        TextView txt_Email = (TextView) headerView.findViewById(R.id.nav_email);

        toggle.syncState();

        txt_Nome.setText(loggedInUser.getNome());
        txt_Email.setText(loggedInUser.getEmail());

        navigationView.setCheckedItem(R.id.nav_caso);

        Bundle extras = getIntent().getExtras();
        int CasoId = extras.getInt("CasoId");
        caso=bd.getCasoPorId(CasoId);

        TextView txt_Titulo = findViewById(R.id.txt_Titulo);
        TextView txt_Descricao = findViewById(R.id.txt_Descricao);
        ImageView img_CasoDetalhes = findViewById(R.id.img_CasoDetalhes);
        Button btn_EliminarCaso = findViewById(R.id.btn_EliminarCaso);

        txt_Titulo.setText(caso.getTitulo());
        txt_Descricao.setText(caso.getDescricao());
        if(!caso.getFotografia().equals("")){
            byte[] decodedByte = Base64.decode(caso.getFotografia(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            img_CasoDetalhes.setImageBitmap(bitmap);
        }

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
                                Intent intent = new Intent(getApplicationContext(), EstacionamentoADecorrer.class);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.nav_home:
                Intent intent = new Intent(getApplicationContext(), EstacionamentoADecorrer.class);
                startActivity(intent);
            case R.id.nav_caso:
                break;
            case R.id.nav_terminar_estacionamento:
                showMessageOKCancel("Tem a certeza que quer finalizar o estacionamento?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finalizarEstacionamento();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavigationView navigationView = findViewById(R.id.nav_view_editar_caso);
                                navigationView.setCheckedItem(R.id.nav_caso);
                            }
                        });
                break;
            case R.id.nav_logout:
                showMessageOKCancel("Se fizer logout o estacionamento continuará ativo! Tem a certeza que quer continuar?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bd.logoutLocal();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }
                        }, null);
                break;
        }

        return true;
    }

    private void finalizarEstacionamento() {
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
        new AlertDialog.Builder(EditarCaso.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", cancelListener)
                .create()
                .show();
    }

}