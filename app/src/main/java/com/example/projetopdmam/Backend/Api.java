package com.example.projetopdmam.Backend;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface Api {

    //Link da API
    String BASE_URL = "https://alexandre-abreu36.outsystemscloud.com/CarParkingAPI/rest/CarParkingAPI/";

    //Colocar ume header em todos os pedidos para mencionar que o pedido vai em JSON
    @Headers({
            "Content-type: application/json; charset=utf-8"
    })

    @POST("login")
    Call<JsonObject> login(@Body JsonObject body);


    //@GET("Obras/Devolver")
    //Call<JsonObject> getObraPorId(@Query("Id") int Id);

    @POST("entrada")
    Call<JsonObject> entrada(@Body JsonObject body);

    @POST("saida")
    Call<JsonObject> saida(@Query("UtilizadorId") int UtilizadorId);

    //@GET("Inspecoes/Devolver")
    //Call<JsonObject> getInspecaoPorId(@Query("Id") int Id);

    @GET("estacionamentos")
    Call<JsonObject> getEstacionamentoAtivoPorIdUtilizador(@Query("UtilizadorId") int UtilizadorId);

    //@GET("Inspecoes/DevolverAtivaPorIdObra")
    //Call<JsonObject> getInspecaoAtivaPorIdObra(@Query("Id") int IdObra);

    //@GET("Inspecoes/DevolverAtivaPorIdInspetorObra")
    //Call<JsonObject> getInspecaoAtivaPorIdInspetorIdObra(@Query("IdInspetor") int IdInspetor, @Query("IdObra") int IdObra);

    //@DELETE("Inspecoes/Cancelar")
    //Call<JsonObject> cancelarInspecao(@Query("Id") int Id);

    @GET("casos")
    Call<JsonObject> getCasoPorId(@Query("EstacionamentoId") int EstacionamentoId);

    @POST("casos/criar")
    Call<JsonObject> criarCaso(@Body JsonObject body);

    @PUT("casos/editar")
    Call<JsonObject> editarCaso(@Body JsonObject body);

    //@GET("Casos/DevolverPorInspecaoId")
    //Call<JsonObject> getCasosPorInspecaoId(@Query("Id") int Id);

    @DELETE("casos/eliminar")
    Call<JsonObject> eliminarCaso(@Query("EstacionamentoId") int EstacionamentoId);

}
