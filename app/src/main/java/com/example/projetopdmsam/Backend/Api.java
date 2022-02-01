package com.example.projetopdmsam.Backend;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://personal-a1j7okp9.outsystemscloud.com/Renergy/rest/API/";

    @Headers({
            "Content-type: application/json; charset=utf-8"
    })

    @POST("Login")
    Call<JsonObject> login(@Body JsonObject body);

    @GET("Utilizadores/Devolver")
    Call<JsonObject> getUtilizadorPorId(@Query("Id") int Id);

    @GET("Obras/Devolver")
    Call<JsonObject> getObraPorId(@Query("Id") int Id);

    @POST("Inspecoes/Iniciar")
    Call<JsonObject> iniciarInspecao(@Body JsonObject body);

    @POST("Inspecoes/Terminar")
    Call<JsonObject> terminarInspecao(@Query("Id") int Id);

    @GET("Inspecoes/Devolver")
    Call<JsonObject> getInspecaoPorId(@Query("Id") int Id);

    @GET("Inspecoes/DevolverAtivaPorIdInspetor")
    Call<JsonObject> getInspecaoAtivaPorIdInspetor(@Query("Id") int IdInspetor);

    @GET("Inspecoes/DevolverAtivaPorIdObra")
    Call<JsonObject> getInspecaoAtivaPorIdObra(@Query("Id") int IdObra);

    @GET("Inspecoes/DevolverAtivaPorIdInspetorObra")
    Call<JsonObject> getInspecaoAtivaPorIdInspetorIdObra(@Query("IdInspetor") int IdInspetor, @Query("IdObra") int IdObra);

    @DELETE("Inspecoes/Cancelar")
    Call<JsonObject> cancelarInspecao(@Query("Id") int Id);

    @POST("Casos/Criar")
    Call<JsonObject> criarCaso(@Body JsonObject body);

    @GET("Casos/Devolver")
    Call<JsonObject> getCasoPorId(@Query("Id") int Id);

    @GET("Casos/DevolverPorInspecaoId")
    Call<JsonObject> getCasosPorInspecaoId(@Query("Id") int Id);

    @DELETE("Casos/Eliminar")
    Call<JsonObject> eliminarCaso(@Query("Id") int Id);

}
