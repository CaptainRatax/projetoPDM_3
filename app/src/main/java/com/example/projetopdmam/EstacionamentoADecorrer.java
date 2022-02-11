package com.example.projetopdmam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
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
import com.example.projetopdmam.Modelos.Estacionamento;
import com.example.projetopdmam.Modelos.Lugar;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstacionamentoADecorrer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    BaseDados bd = new BaseDados(this);

    private DrawerLayout drawer;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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

        navigationView.setCheckedItem(R.id.nav_home);

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

        TextView txt_codigoEstacionamentoInicio = findViewById(R.id.txt_codigoEstacionamentoInicio);
        TextView txt_nomeUtilizadorInicio = findViewById(R.id.txt_nomeUtilizadorInicio);
        TextView txt_dataInicio = findViewById(R.id.txt_dataInicio);
        TextView txt_casoAtivoDesativo = findViewById(R.id.txt_casoAtivoDesativo);
        TextView txt_titulo_caso_inicio = findViewById(R.id.txt_titulo_caso_inicio);
        TextView txt_descricao_caso_inicio = findViewById(R.id.txt_descricao_caso_inicio);
        ImageView img_CasoInicio = findViewById(R.id.img_CasoInicio);
        Button btn_FinalizarEstacionamento = findViewById(R.id.btn_FinalizarEstacionamento);

        txt_codigoEstacionamentoInicio.setText(lugar.getCodigo());
        txt_nomeUtilizadorInicio.setText(loggedInUser.getNome());

        String data = estacionamentoADecorrer.getDataEntrada();
        String dataFormatada =
                data.substring(8, 10) + "/" +  data.substring(5, 7) + "/" + data.substring(0, 4) + " - " +
                data.substring(11,13) + ":" + data.substring(14, 16) + ":" + data.substring(17, 19);
        txt_dataInicio.setText(dataFormatada);

        if(caso.isActive()){
            txt_casoAtivoDesativo.setText("Caso associado:");
            txt_titulo_caso_inicio.setVisibility(View.VISIBLE);
            txt_descricao_caso_inicio.setVisibility(View.VISIBLE);

            txt_titulo_caso_inicio.setText(caso.getTitulo());
            txt_descricao_caso_inicio.setText(caso.getDescricao());

            if(!caso.getFotografia().equals("")){
                img_CasoInicio.setVisibility(View.VISIBLE);
                byte[] decodedByte = Base64.decode(caso.getFotografia(), 0);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                img_CasoInicio.setImageBitmap(bitmap);
            }else{
                img_CasoInicio.setVisibility(View.GONE);
            }
        }else{
            txt_casoAtivoDesativo.setText("Nenhum caso criado...");
            txt_titulo_caso_inicio.setVisibility(View.GONE);
            txt_descricao_caso_inicio.setVisibility(View.GONE);
            img_CasoInicio.setVisibility(View.GONE);
        }

        btn_FinalizarEstacionamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("Tem a certeza que quer finalizar o estacionamento?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finalizarEstacionamento();
                            }
                        },null);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_caso:
                if(caso.isActive()){
                    Intent intent2 = new Intent(getApplicationContext(), EditCaso.class);
                    startActivity(intent2);
                }else{
                    Intent intent = new Intent(getApplicationContext(), NovoCaso.class);
                    startActivity(intent);
                }
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
                                NavigationView navigationView = findViewById(R.id.nav_view);
                                navigationView.setCheckedItem(R.id.nav_home);
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

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
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
        new AlertDialog.Builder(EstacionamentoADecorrer.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", cancelListener)
                .create()
                .show();
    }
}