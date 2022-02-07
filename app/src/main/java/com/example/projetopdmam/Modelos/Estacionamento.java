package com.example.projetopdmam.Modelos;

public class Estacionamento {

    int Id;
    int UtilizadorId;
    int LugarId;
    String DataEntrada;
    String DataSaida;
    boolean EstacionamentoLivre;
    boolean IsActive;

    public Estacionamento(){

    }

    public Estacionamento(int id, int utilizadorId, int lugarId, String dataEntrada, String dataSaida, boolean estacionamentoLivre, boolean isActive) {
        Id = id;
        UtilizadorId = utilizadorId;
        LugarId = lugarId;
        DataEntrada = dataEntrada;
        DataSaida = dataSaida;
        EstacionamentoLivre = estacionamentoLivre;
        IsActive = isActive;
    }

    public Estacionamento(int utilizadorId, int lugarId, String dataEntrada, String dataSaida, boolean estacionamentoLivre, boolean isActive) {
        UtilizadorId = utilizadorId;
        LugarId = lugarId;
        DataEntrada = dataEntrada;
        DataSaida = dataSaida;
        EstacionamentoLivre = estacionamentoLivre;
        IsActive = isActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getUtilizadorId() {
        return UtilizadorId;
    }

    public void setUtilizadorId(int utilizadorId) {
        UtilizadorId = utilizadorId;
    }

    public int getLugarId() {
        return LugarId;
    }

    public void setLugarId(int lugarId) {
        LugarId = lugarId;
    }

    public String getDataEntrada() {
        return DataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        DataEntrada = dataEntrada;
    }

    public String getDataSaida() {
        return DataSaida;
    }

    public void setDataSaida(String dataSaida) {
        DataSaida = dataSaida;
    }

    public boolean isEstacionamentoLivre() {
        return EstacionamentoLivre;
    }

    public void setEstacionamentoLivre(boolean estacionamentoLivre) {
        EstacionamentoLivre = estacionamentoLivre;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
