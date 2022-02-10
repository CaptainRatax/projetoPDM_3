package com.example.projetopdmam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Estacionamento;
import com.example.projetopdmam.Modelos.Lugar;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRCodeReader extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    BaseDados bd = new BaseDados(this);

    private ZXingScannerView scannerView;
    private TextView txtResult;

    Utilizador loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_qrcode_reader);

        loggedInUser = bd.getLoggedInUser();
        //Inicialização
        scannerView = (ZXingScannerView) findViewById(R.id.zxscan);
        txtResult = (TextView) findViewById(R.id.txt_result);

        //Pedir permissões de câmera
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(QRCodeReader.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QRCodeReader.this, "É necessário aceitar a permissão de acesso à câmara!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        //Aqui é recebido o resultado do código QR
        try {
            txtResult.setText(rawResult.getText()); //Altera o texto na parte de baixo do ecrã para o que foi lido no QR Code
                Lugar lugar = new Lugar();
                    if(isInternetAvailable()){
                        //Chamada à API para verificar se o id do lugar existe

                        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getLugarPorCodigo(rawResult.getText());
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if(response.body().get("Sucesso").getAsBoolean()){//Verifica se o pedido foi bem sucedido, ou seja se existe algum lugar com aquele codigo
                                    //Existe um lugar com aquele codigo

                                    //Guarda o lugar numa variável local
                                    JsonObject lugarJson = response.body().get("Lugar").getAsJsonObject();
                                    lugar.setId(lugarJson.get("Id").getAsInt());
                                    lugar.setCodigo(lugarJson.get("Codigo").getAsString());
                                    lugar.setAndar(lugarJson.get("Andar").getAsString());
                                    lugar.setActive(true);

                                    //Chamada à API para ver se o lugar já está a ser usado num estacionamento no momento
                                    Call<JsonObject> call1 = RetrofitClient.getInstance().getMyApi().getEstacionamentoAtivoPorIdLugar(lugar.getId());
                                    call1.enqueue(new Callback<JsonObject>() {
                                        @Override
                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                            if(response.body().get("Sucesso").getAsBoolean()){//Verifica se o pedido foi bem sucedido, ou seja se o lugar está a ser usado num estacionamento no momento
                                                //O lugar está a ser usado num estacionamento no momento
                                                if(response.body().get("Estacionamento").getAsJsonObject().get("UtilizadorId").getAsInt() == loggedInUser.getId()){
                                                    //Se o id do utilizador que está a uesar o estacionamento nesse lugar for igual ao do loggedInUser
                                                    comecarEstacionamento(lugar, response.body().get("Estacionamento").getAsJsonObject()); //Usa uma função auxiliar para começar o estacionamento localmente (a função está mais em baixo)
                                                }else{
                                                    //O lugar está a ser usado por outra pessoa
                                                    Toast.makeText(getApplicationContext(), "Esse lugar já está a ser usado noutro estacionamento", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                                    startActivity(intent);
                                                }
                                            }else{
                                                //O lugar não está a ser usado num estacionamento no momento
                                                showMessageOKCancel("Tem a certeza que quer dar entrada no estacionamento no lugar \"" + lugar.getCodigo() + "?",
                                                    new DialogInterface.OnClickListener() { //Listener do click no botão sim
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //Utilizador clicou no botão sim

                                                            //Chamada à API para dar entrada no estacionamento no servidor
                                                            String request = "{ \"UtilizadorId\":  \"" + loggedInUser.getId() + "\", \"LugarId\": \"" + lugar.getId() + "\"}";
                                                            JsonObject bodyJson = new JsonParser().parse(request).getAsJsonObject();
                                                            Call<JsonObject> call2 = RetrofitClient.getInstance().getMyApi().entrada(bodyJson);
                                                            call2.enqueue(new Callback<JsonObject>() {
                                                                @Override
                                                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                                    if(response.body().get("Sucesso").getAsBoolean()){
                                                                        //O server aceitou o estacionamento com sucesso
                                                                        comecarEstacionamento(lugar, response.body().get("Estacionamento").getAsJsonObject()); //Usa uma função auxiliar para começar o estacionamento localmente (a função está mais em baixo)
                                                                    }else{
                                                                        //O server não começou o estacionamento por algum motivo
                                                                        Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                                @Override
                                                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                                                    //Aconteceu alguma coisa de errado por parte do servidor
                                                                    Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar o estacionamento", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    },
                                                    new DialogInterface.OnClickListener() { //Listener do click no botão não
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //Utilizador clicou no botão não

                                                            txtResult.setText("...");
                                                            scannerView.setResultHandler(QRCodeReader.this);
                                                            scannerView.startCamera();
                                                        }
                                                    });
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<JsonObject> call, Throwable t) {
                                            //Aconteceu alguma coisa de errado por parte do servidor
                                            Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar o estacionamento", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                            startActivity(intent);
                                        }
                                    });
                                }else{
                                    //Não existe nenhum lugar com o codigo fornecido
                                    Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_SHORT).show();
                                    qrCodeInvalido();
                                }
                            }
                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                //Aconteceu alguma coisa de errado por parte do servidor
                                Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar o estacionamento", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                startActivity(intent);
                            }
                        });
                    }else{
                        //Não existe conexão à internet
                        Toast.makeText(QRCodeReader.this, "É necessário uma conexão à internet...", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                        startActivity(intent);
                    }
        }catch (Exception error){
            //Aconteceu alguma coisa durante a execução do código
            Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar o estacionamento", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
            startActivity(intent);
        }

    }

    //Função auxiliar usada para reciclar código usado duas vezes para começar o estacionamento localmente
    private void comecarEstacionamento(Lugar lugar, JsonObject estacionamento){
        //Cria um objeto do tipo Estacionamento usando o Json que recebeu da API
        Estacionamento estacionamentoADecorrer = new Estacionamento();
        estacionamentoADecorrer.setId(estacionamento.get("Id").getAsInt());
        estacionamentoADecorrer.setDataEntrada(estacionamento.get("DataEntrada").getAsString());
        estacionamentoADecorrer.setDataSaida(estacionamento.get("DataSaida").getAsString());
        estacionamentoADecorrer.setEstacionamentoLivre(estacionamento.get("EstacionamentoLivre").getAsBoolean());
        estacionamentoADecorrer.setUtilizadorId(estacionamento.get("UtilizadorId").getAsInt());
        estacionamentoADecorrer.setLugarId(estacionamento.get("LugarId").getAsInt());
        estacionamentoADecorrer.setActive(true);
        if (bd.getEstacionamentoADecorrer().isActive()) { //Verifica se existe algum estacionamento a decorrer localmente
            //Existe um estacionamento a decorrer localmente
            if (bd.getEstacionamentoADecorrer() != estacionamentoADecorrer) { //Verifica se o estacionamento que está a decorrer localmente é diferente do recebido
                bd.acabarEstacionamentoLocal(); //Se for acaba o estacionamento local
                bd.comecarEstacionamentoLocal(estacionamentoADecorrer); //e começa um novo com os dados do estacionamento recebido
            }
        } else {
            //Não existe nenhum estacionamento a decorrer localmente
            bd.comecarEstacionamentoLocal(estacionamentoADecorrer); //Começa o estacionamento localmente
        }
        if (bd.getLugarLocal().isActive()) {//Verifica se o lugar já existe localmente
            //O lugar existe localmente
            if (bd.getLugarLocal() != lugar) {//Verifica se o lugar que existe localmente é diferente do recebido
                bd.editarLugar(lugar); //Se for altera o lugar local e coloca os dados do lugar recebido
            }
        } else {
            //O lugar não existe localmente
            bd.adicionarLugar(lugar); //Cria o lugar localmente
        }

        Intent intent = new Intent(getApplicationContext(), EstacionamentoADecorrer.class);
        startActivity(intent);
    }


    //Funções auxiliares

    private void qrCodeInvalido(){
        showMessageOKCancel("QR Code inválido! Quer tentar outra vez?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Utilizador clicou no botão sim

                txtResult.setText("...");
                scannerView.setResultHandler(QRCodeReader.this);
                scannerView.startCamera();

            }
        }, new DialogInterface.OnClickListener() {//Listener do click no botão não
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Utilizador clicou no botão não

                Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                startActivity(intent);
            }
        });
    }

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
        new AlertDialog.Builder(QRCodeReader.this)
                .setMessage(message)
                .setPositiveButton("Sim", okListener)
                .setNegativeButton("Não", cancelListener)
                .create()
                .show();
    }

}