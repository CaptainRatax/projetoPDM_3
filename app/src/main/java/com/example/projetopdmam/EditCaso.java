package com.example.projetopdmam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Caso;
import com.example.projetopdmam.Modelos.Estacionamento;
import com.example.projetopdmam.Modelos.Lugar;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCaso extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    BaseDados bd = new BaseDados(this);

    private DrawerLayout drawer;

    Utilizador loggedInUser;
    Estacionamento estacionamentoADecorrer;
    Lugar lugar;
    String fotografiaBase64 = "";
    Caso caso;
    private static final int IMG_REQUEST = 21;

    ImageView img_Caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_edit_caso);
        loggedInUser=bd.getLoggedInUser();
        estacionamentoADecorrer =bd.getEstacionamentoADecorrer();
        lugar =bd.getLugarLocal();
        caso = bd.getCasoPorIdEstacionamento(estacionamentoADecorrer.getId());

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

        Button btn_GuardarCaso = findViewById(R.id.btn_GuardarCasoEditar);
        EditText edt_TituloCaso = findViewById(R.id.edt_TituloCasoEditar);
        EditText edt_DescricaoCaso = findViewById(R.id.edt_DescricaoCasoEditar);
        img_Caso = findViewById(R.id.img_CasoEditar);

        edt_TituloCaso.setText(caso.getTitulo());
        edt_DescricaoCaso.setText(caso.getDescricao());

        if(!caso.getFotografia().equals("")){
            byte[] decodedByte = Base64.decode(caso.getFotografia(), 0);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            img_Caso.setImageBitmap(bitmap);
        }

        img_Caso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fotografiaBase64 ==""){
                    Intent takePictureIntent = new Intent();
                    takePictureIntent.setType("image/*");
                    takePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                    try {
                        startActivityForResult(takePictureIntent, IMG_REQUEST);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro a abrir a galeria.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showMessageOKCancel("Quer alterar a imagem do caso?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent takePictureIntent = new Intent();
                            takePictureIntent.setType("image/*");
                            takePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                            try {
                                startActivityForResult(takePictureIntent, IMG_REQUEST);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(getApplicationContext(), "Ocorreu um erro a abrir a galeria.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, null);
                }
            }
        });

        Button btn_RemoverImagem = findViewById(R.id.btn_RemoverImagemEditar);
        btn_RemoverImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("Tem a certeza que quer remover a imagem do caso?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fotografiaBase64 = "";
                        img_Caso.setImageResource(R.drawable.ic_add_image_icon_icons_com_54218);
                        btn_RemoverImagem.setVisibility(View.GONE);
                    }
                }, null);
            }
        });

        btn_GuardarCaso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = edt_TituloCaso.getText().toString();
                String descricao = edt_DescricaoCaso.getText().toString();
                if(!titulo.equals("")){
                    if(!descricao.equals("")){
                        if(isInternetAvailable()){
                            String request = "{"
                                    + " \"EstacionamentoId\": " + estacionamentoADecorrer.getId() + ", "
                                    + "\"Titulo\": \"" + titulo + "\", "
                                    + "\"Descricao\": \"" + descricao + "\", "
                                    + "\"FotografiaBase64\": \"" + fotografiaBase64 + "\" }";
                            JsonObject body = new JsonParser().parse(request).getAsJsonObject();
                            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().editarCaso(body);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.body().get("Sucesso").getAsBoolean()){
                                        JsonObject casoJson = response.body().get("Caso").getAsJsonObject();
                                        Caso caso = new Caso();
                                        caso.setId(casoJson.get("Id").getAsInt());
                                        caso.setEstacionamentoId(casoJson.get("EstacionamentoId").getAsInt());
                                        caso.setTitulo(casoJson.get("Titulo").getAsString());
                                        caso.setDescricao(casoJson.get("Descricao").getAsString());
                                        caso.setFotografia(casoJson.get("Fotografia").getAsString());
                                        caso.setActive(true);
                                        bd.editarCaso(caso);
                                        Toast.makeText(getApplicationContext(), "Caso editado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), EstacionamentoADecorrer.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Aconteceu algo errado ao tentar criar o caso", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "É necessária uma conexão à internet para efetuar essa operação!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "A descrição do caso não pode estar vazia!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "O título do caso não pode estar vazio!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                fotografiaBase64 = Base64.encodeToString(b, Base64.DEFAULT);
                img_Caso.setImageBitmap(imageBitmap);
                Button btn_RemoverImagem = findViewById(R.id.btn_RemoverImagemEditar);
                btn_RemoverImagem.setVisibility(View.VISIBLE);
            }catch (IOException e){
                Toast.makeText(this, "Aconteceu algo errado ao tentar adicionar a imagem", Toast.LENGTH_LONG).show();
            }
        }
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
                                NavigationView navigationView = findViewById(R.id.nav_view_novo_caso);
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
        new AlertDialog.Builder(EditCaso.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", cancelListener)
                .create()
                .show();
    }

}