package com.example.projetopdmsam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetopdmsam.Backend.BaseDados;
import com.example.projetopdmsam.Backend.RetrofitClient;
import com.example.projetopdmsam.Modelos.Inspecao;
import com.example.projetopdmsam.Modelos.Obra;
import com.example.projetopdmsam.Modelos.Utilizador;
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
            if(isNumeric(rawResult.getText())){
                int id = Integer.parseInt(rawResult.getText());
                Obra obra = new Obra();
                if(id > 0){
                    if(isInternetAvailable()){
                        //Chamada à API para verificar se o id da obra existe
                        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getObraPorId(id);
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
                                    Call<JsonObject> call1 = RetrofitClient.getInstance().getMyApi().getInspecaoAtivaPorIdObra(id);
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
                                                    Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                                    startActivity(intent);
                                                }
                                            }else{
                                                //A obra não está a ser inspecionada no momento
                                                showMessageOKCancel("Tem a certeza que quer iniciar a inspeção à obra \"" + obra.getNome() + "\" responsável por " + obra.getResponsavel() + "?",
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
                                                                        Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                                @Override
                                                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                                                    //Aconteceu alguma coisa de errado por parte do servidor
                                                                    Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
                                            startActivity(intent);
                                        }
                                    });
                                }else{
                                    //Não existe nenhuma obra com o id fornecido
                                    Toast.makeText(getApplicationContext(), response.body().get("Mensagem").getAsString(), Toast.LENGTH_SHORT).show();
                                    qrCodeInvalido();
                                }
                            }
                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                //Aconteceu alguma coisa de errado por parte do servidor
                                Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
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
                }else{
                    //O id fornecido é um número negativo
                    qrCodeInvalido();
                }
            }else{
                //O qr code lido não devolveu um número inteiro
                qrCodeInvalido();
            }
        }catch (Exception error){
            //Aconteceu alguma coisa durante a execução do código
            Toast.makeText(getApplicationContext(), "Aconteceu algo de errado ao tentar iniciar a inspeção", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PaginaInicial.class);
            startActivity(intent);
        }

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