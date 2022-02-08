package com.example.projetopdmam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetopdmam.Backend.BaseDados;
import com.example.projetopdmam.Backend.RetrofitClient;
import com.example.projetopdmam.Modelos.Utilizador;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    BaseDados bd = new BaseDados(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Utilizador loggedInUser = bd.getLoggedInUser();

        if(loggedInUser.isActive()){
            Intent intent = new Intent(getApplicationContext(),PaginaInicial.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.login_main);

        Button btn_Login = findViewById(R.id.btn_Login);
        EditText edt_Email = findViewById(R.id.edt_Email);
        EditText edt_Password = findViewById(R.id.edt_Password);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_Email.getText().toString();
                String password = edt_Password.getText().toString();
                if(isInternetAvailable()){
                    String request = "{ \"Email\":  \"" + email + "\", \"Password\": \"" + password + "\"}";
                    JsonObject body = new JsonParser().parse(request).getAsJsonObject();
                    Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().login(body);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if(response.body() != null){
                                if(response.body().get("Sucesso").getAsBoolean()){
                                    JsonObject utilizador = response.body().get("Utilizador").getAsJsonObject();
                                    Utilizador loggedInUser = new Utilizador();
                                    loggedInUser.setId(utilizador.get("Id").getAsInt());
                                    loggedInUser.setNome(utilizador.get("Nome").getAsString());
                                    loggedInUser.setMorada(utilizador.get("Morada").getAsString());
                                    loggedInUser.setCodigoPostal(utilizador.get("CodigoPostal").getAsString());
                                    loggedInUser.setTelemovel(utilizador.get("NumeroTelemovel").getAsString());
                                    loggedInUser.setNIF(utilizador.get("NIF").getAsString());
                                    loggedInUser.setEmail(utilizador.get("Email").getAsString());
                                    loggedInUser.setPassword(utilizador.get("Password").getAsString());
                                    loggedInUser.setActive(true);
                                    bd.loginLocal(loggedInUser);
                                    Intent intent = new Intent(getApplicationContext(),PaginaInicial.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(Login.this, response.body().get("Mensagem").getAsString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(Login.this, "Aconteceu algo errado ao tentar efetuar o login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private boolean isInternetAvailable(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

}