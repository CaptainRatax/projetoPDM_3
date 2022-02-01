package com.example.projetopdmsam;

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
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmsam.Backend.BaseDados;
import com.example.projetopdmsam.Backend.RetrofitClient;
import com.example.projetopdmsam.Modelos.Inspecao;
import com.example.projetopdmsam.Modelos.Obra;
import com.example.projetopdmsam.Modelos.Utilizador;
import com.example.projetopdmsam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

        Inspecao inspecaoADecorrer = bd.getInspecaoADecorrer();

        if(inspecaoADecorrer.isActive()){
            Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
            startActivity(intent);
        }else{
            if(isInternetAvailable()){
                Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getInspecaoAtivaPorIdInspetor(loggedInUser.getId());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.body().get("Success").getAsBoolean()){
                            JsonObject obraJson = response.body().get("Obra").getAsJsonObject();
                            //Cria um objeto do tipo Obra usando o Json que recebeu da API
                            Obra obra = new Obra();
                            obra.setId(obraJson.get("Id").getAsInt());
                            obra.setNome(obraJson.get("Nome").getAsString());
                            obra.setDescricao(obraJson.get("Descricao").getAsString());
                            obra.setMorada(obraJson.get("Morada").getAsString());
                            obra.setCodigoPostal(obraJson.get("CodigoPostal").getAsString());
                            obra.setLocalidade(obraJson.get("Localidade").getAsString());
                            obra.setPais(obraJson.get("Pais").getAsString());
                            obra.setDataInicio(obraJson.get("DataInicio").getAsString());
                            obra.setResponsavel(obraJson.get("Responsavel").getAsString());
                            obra.setActive(obraJson.get("IsActive").getAsBoolean());

                            JsonObject inspecao = response.body().get("Inspecao").getAsJsonObject();
                            //Cria um objeto do tipo Inspecao usando o Json que recebeu da API
                            inspecaoADecorrer.setId(inspecao.get("Id").getAsInt());
                            inspecaoADecorrer.setDataInicio(inspecao.get("DataInicio").getAsString());
                            inspecaoADecorrer.setDataFim(inspecao.get("DataFim").getAsString());
                            inspecaoADecorrer.setFinished(inspecao.get("IsFinished").getAsBoolean());
                            inspecaoADecorrer.setInspetorId(inspecao.get("InspectorId").getAsInt());
                            inspecaoADecorrer.setObraId(inspecao.get("ObraId").getAsInt());
                            inspecaoADecorrer.setActive(inspecao.get("IsActive").getAsBoolean());

                            if (bd.getInspecaoADecorrer().isActive()) { //Verifica se existe alguma inspeção a decorrer localmente
                                //Existe uma inspeção a decorrer localmente
                                if (bd.getInspecaoADecorrer() != inspecaoADecorrer) { //Verifica se a inspeção que está a decorrer localmente é diferente da recebida
                                    bd.acabarInspecaoLocal(); //Se for acaba a inspeção local
                                    bd.comecarInspecaoLocal(inspecaoADecorrer); //e começa uma nova com os dados da inspeção recebida
                                }
                            } else {
                                //Não existe nenhuma inspeção a decorrer localmente
                                bd.comecarInspecaoLocal(inspecaoADecorrer); //Começa a inspeção localmente
                            }
                            if (bd.getObraPorId(obra.getId()).isActive()) {//Verifica se a obra já existe localmente
                                //A obra existe localmente
                                if (bd.getObraPorId(obra.getId()) != obra) {//Verifica se a obra que existe localmente é diferente da recebida
                                    bd.editarObra(obra); //Se for altera a obra local e coloca os dados da obra recebida
                                }
                            } else {
                                //A obra não existe localmente
                                bd.adicionarObra(obra); //Cria a obra localmente
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
        //Button btn_IniciarComId = findViewById(R.id.btn_IniciarComId);

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

        /*btn_IniciarComId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edt_IdObra = findViewById(R.id.edt_IdObra);
                Obra obra = new Obra();
                if(isNumeric(edt_IdObra.getText().toString())){
                    int Id = Integer.parseInt(edt_IdObra.getText().toString());
                    if(Id > 0){
                        if(isInternetAvailable()){
                            //Chamada à API para verificar se o id da obra existe
                            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getObraPorId(Id);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.body().get("Success").getAsBoolean()){//Verifica se o pedido foi bem sucedido, ou seja se existe alguma obra com aquele id
                                        //Existe uma obra com aquele id

                                        //Guarda a obra numa variável local
                                        JsonObject obraJson = response.body().get("Obra").getAsJsonObject();
                                        obra.setId(obraJson.get("Id").getAsInt());
                                        obra.setNome(obraJson.get("Nome").getAsString());
                                        obra.setDescricao(obraJson.get("Descricao").getAsString());
                                        obra.setMorada(obraJson.get("Morada").getAsString());
                                        obra.setCodigoPostal(obraJson.get("CodigoPostal").getAsString());
                                        obra.setLocalidade(obraJson.get("Localidade").getAsString());
                                        obra.setPais(obraJson.get("Pais").getAsString());
                                        obra.setDataInicio(obraJson.get("DataInicio").getAsString());
                                        obra.setResponsavel(obraJson.get("Responsavel").getAsString());
                                        obra.setActive(obraJson.get("IsActive").getAsBoolean());

                                        //Chamada à API para ver se a obra já está a ser inspecionada no momento
                                        Call<JsonObject> call1 = RetrofitClient.getInstance().getMyApi().getInspecaoAtivaPorIdObra(Id);
                                        call1.enqueue(new Callback<JsonObject>() {
                                            @Override
                                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                if(response.body().get("Success").getAsBoolean()){//Verifica se o pedido foi bem sucedido, ou seja se a obra está a ser inspecionada no momento
                                                    //A obra está a ser inspecionada no momento
                                                    if(response.body().get("Inspecao").getAsJsonObject().get("InspectorId").getAsInt() == loggedInUser.getId()){
                                                        //Se o id do inspetor que está a inspecionar essa obra for igual ao do loggedInUser
                                                        comecarInspecao(obra, response.body().get("Inspecao").getAsJsonObject()); //Usa uma função auxiliar para começar a inspeção localmente (a função está mais em baixo)
                                                    }else{
                                                        //A obra está a ser inspecionada por outra pessoa
                                                        Toast.makeText(getApplicationContext(), "Essa obra já está a ser inspecionada por outro inspetor", Toast.LENGTH_LONG).show();
                                                        edt_IdObra.setText("");
                                                    }
                                                }else{
                                                    //A obra não está a ser inspecionada no momento
                                                    showMessageSimNao("Tem a certeza que quer iniciar a inspeção à obra \"" + obra.getNome() + "\" responsável por " + obra.getResponsavel() + "?",
                                                            new DialogInterface.OnClickListener() { //Listener do click no botão sim
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    //Utilizador clicou no botão sim

                                                                    //Chamada à API para começar a inspeção no servidor
                                                                    String request = "{ \"InspectorId\":  \"" + loggedInUser.getId() + "\", \"ObraId\": \"" + obra.getId() + "\"}";
                                                                    JsonObject bodyJson = new JsonParser().parse(request).getAsJsonObject();
                                                                    Call<JsonObject> call2 = RetrofitClient.getInstance().getMyApi().iniciarInspecao(bodyJson);
                                                                    call2.enqueue(new Callback<JsonObject>() {
                                                                        @Override
                                                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                                            if(response.body().get("Success").getAsBoolean()){
                                                                                //O server aceitou a inspeção com sucesso
                                                                                comecarInspecao(obra, response.body().get("Inspecao").getAsJsonObject()); //Usa uma função auxiliar para começar a inspeção localmente (a função está mais em baixo)
                                                                            }else{
                                                                                //O server não começou a inspeção por algum motivo
                                                                                Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_SHORT).show();
                                                                                edt_IdObra.setText("");
                                                                            }
                                                                        }
                                                                        @Override
                                                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                                                            //Aconteceu alguma coisa de errado por parte do servidor
                                                                            Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
                                                                            edt_IdObra.setText("");
                                                                        }
                                                                    });
                                                                }
                                                            },
                                                            new DialogInterface.OnClickListener() { //Listener do click no botão não
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    //Utilizador clicou no botão não
                                                                    edt_IdObra.setText("");
                                                                }
                                                            });
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                                //Aconteceu alguma coisa de errado por parte do servidor
                                                Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
                                                edt_IdObra.setText("");
                                            }
                                        });
                                    }else{
                                        //Não existe nenhuma obra com o id fornecido
                                        Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_SHORT).show();
                                        edt_IdObra.setText("");
                                    }
                                }
                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    //Aconteceu alguma coisa de errado por parte do servidor
                                    Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
                                    edt_IdObra.setText("");
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "É necessária uma conexão à internet para efetuar essa operação!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "O id é inválido!", Toast.LENGTH_SHORT).show();
                        edt_IdObra.setText("");
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "O id é inválido!", Toast.LENGTH_SHORT).show();
                    edt_IdObra.setText("");
                }
            }
        });*/

    }

    //Função auxiliar usada para reciclar código usado duas vezes para começar a inspeção localmente
    private void comecarInspecao(Obra obra, JsonObject inspecao){
        //Cria um objeto do tipo Inspecao usando o Json que recebeu da API
        Inspecao inspecaoADecorrer = new Inspecao();
        inspecaoADecorrer.setId(inspecao.get("Id").getAsInt());
        inspecaoADecorrer.setDataInicio(inspecao.get("DataInicio").getAsString());
        inspecaoADecorrer.setDataFim(inspecao.get("DataFim").getAsString());
        inspecaoADecorrer.setFinished(inspecao.get("IsFinished").getAsBoolean());
        inspecaoADecorrer.setInspetorId(inspecao.get("InspectorId").getAsInt());
        inspecaoADecorrer.setObraId(inspecao.get("ObraId").getAsInt());
        inspecaoADecorrer.setActive(inspecao.get("IsActive").getAsBoolean());
        if (bd.getInspecaoADecorrer().isActive()) { //Verifica se existe alguma inspeção a decorrer localmente
            //Existe uma inspeção a decorrer localmente
            if (bd.getInspecaoADecorrer() != inspecaoADecorrer) { //Verifica se a inspeção que está a decorrer localmente é diferente da recebida
                bd.acabarInspecaoLocal(); //Se for acaba a inspeção local
                bd.comecarInspecaoLocal(inspecaoADecorrer); //e começa uma nova com os dados da inspeção recebida
            }
        } else {
            //Não existe nenhuma inspeção a decorrer localmente
            bd.comecarInspecaoLocal(inspecaoADecorrer); //Começa a inspeção localmente
        }
        if (bd.getObraPorId(obra.getId()).isActive()) {//Verifica se a obra já existe localmente
            //A obra existe localmente
            if (bd.getObraPorId(obra.getId()) != obra) {//Verifica se a obra que existe localmente é diferente da recebida
                bd.editarObra(obra); //Se for altera a obra local e coloca os dados da obra recebida
            }
        } else {
            //A obra não existe localmente
            bd.adicionarObra(obra); //Cria a obra localmente
        }

        Intent intent = new Intent(getApplicationContext(), InspecaoADecorrer.class);
        startActivity(intent);
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

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}