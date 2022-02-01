package com.example.projetopdmsam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projetopdmsam.Backend.BaseDados;
import com.example.projetopdmsam.Backend.RetrofitClient;
import com.example.projetopdmsam.Modelos.Utilizador;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.internal.Util;
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
        EditText edt_Username = findViewById(R.id.edt_Username);
        EditText edt_Password = findViewById(R.id.edt_Password);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edt_Username.getText().toString();
                String password = edt_Password.getText().toString();
                password = sha512(password);
                if(isInternetAvailable()){
                    String request = "{ \"Username\":  \"" + username + "\", \"Password\": \"" + password + "\"}";
                    JsonObject body = new JsonParser().parse(request).getAsJsonObject();
                    Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().login(body);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if(response.body() != null){
                                if(response.body().get("IsAuthorized").getAsBoolean()){
                                    JsonObject utilizador = response.body().get("Utilizador").getAsJsonObject();
                                    Utilizador loggedInUser = new Utilizador();
                                    loggedInUser.setId(utilizador.get("Id").getAsInt());
                                    loggedInUser.setNome(utilizador.get("Nome").getAsString());
                                    loggedInUser.setUsername(utilizador.get("Username").getAsString());
                                    loggedInUser.setPassword(utilizador.get("Password").getAsString());
                                    loggedInUser.setEmail(utilizador.get("Email").getAsString());
                                    loggedInUser.setTelemovel(utilizador.get("Telemovel").getAsString());
                                    loggedInUser.setActive(utilizador.get("IsActive").getAsBoolean());
                                    bd.loginLocal(loggedInUser);
                                    Intent intent = new Intent(getApplicationContext(),PaginaInicial.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(Login.this, response.body().get("Message").getAsString(), Toast.LENGTH_SHORT).show();
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

    /*Funções auxiliares*/

    private static String sha512(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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

}