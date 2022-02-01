package com.example.projetopdmsam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projetopdmsam.Backend.BaseDados;
import com.example.projetopdmsam.Backend.RetrofitClient;
import com.example.projetopdmsam.Modelos.Caso;
import com.example.projetopdmsam.Modelos.Inspecao;
import com.example.projetopdmsam.Modelos.Obra;
import com.example.projetopdmsam.Modelos.Utilizador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NovoCaso extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    Utilizador loggedInUser;
    Inspecao inspecaoADecorrer;
    Obra obra;
    String imagemBase64 = "";
    private static final int IMG_REQUEST = 21;

    ImageView img_Caso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_novo_caso);
        loggedInUser=bd.getLoggedInUser();
        inspecaoADecorrer=bd.getInspecaoADecorrer();
        obra=bd.getObraPorId(inspecaoADecorrer.getObraId());

        FloatingActionButton btn_Voltar = findViewById(R.id.btn_Voltar);
        Button btn_GuardarCaso = findViewById(R.id.btn_GuardarCaso);
        EditText edt_TituloCaso = findViewById(R.id.edt_TituloCaso);
        EditText edt_DescricaoCaso = findViewById(R.id.edt_DescricaoCaso);
        img_Caso = findViewById(R.id.img_Caso);

        btn_Voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
                startActivity(intent);
            }
        });

        img_Caso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imagemBase64==""){
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
                    });
                }
            }
        });

        Button btn_RemoverImagem = findViewById(R.id.btn_RemoverImagem);
        btn_RemoverImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("Tem a certeza que quer remover a imagem do caso?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imagemBase64 = "";
                        img_Caso.setImageResource(R.drawable.ic_add_image_icon_icons_com_54218);
                        btn_RemoverImagem.setVisibility(View.GONE);
                    }
                });
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
                                    + " \"Titulo\": \"" + titulo + "\", "
                                    + "\"Descricao\": \"" + descricao + "\", "
                                    + "\"InspecaoId\": " + inspecaoADecorrer.getId() + ", "
                                    + "\"Imagem\": \"" + imagemBase64 + "\" }";
                            JsonObject body = new JsonParser().parse(request).getAsJsonObject();
                            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().criarCaso(body);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.body().get("Success").getAsBoolean()){
                                        JsonObject casoJson = response.body().get("Caso").getAsJsonObject();
                                        Caso caso = new Caso();
                                        caso.setId(casoJson.get("Id").getAsInt());
                                        caso.setTitulo(casoJson.get("Titulo").getAsString());
                                        caso.setDescricao(casoJson.get("Descricao").getAsString());
                                        caso.setImagem(casoJson.get("Imagem").getAsString());
                                        caso.setInspecaoId(casoJson.get("InspecaoId").getAsInt());
                                        bd.adicionarCaso(caso);
                                        Toast.makeText(getApplicationContext(), "Caso criado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
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
                imagemBase64 = Base64.encodeToString(b, Base64.DEFAULT);
                img_Caso.setImageBitmap(imageBitmap);
                Button btn_RemoverImagem = findViewById(R.id.btn_RemoverImagem);
                btn_RemoverImagem.setVisibility(View.VISIBLE);
            }catch (IOException e){
                Toast.makeText(this, "Aconteceu algo errado ao tentar adicionar a imagem", Toast.LENGTH_LONG).show();
            }
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(NovoCaso.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", null)
                .create()
                .show();
    }

}