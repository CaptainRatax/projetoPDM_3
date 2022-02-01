package com.example.projetopdmsam.Modelos;

public class Inspecao {

    int Id;
    String DataInicio;
    String DataFim;
    boolean IsFinished;
    int InspetorId;
    int ObraId;
    boolean IsActive;

    public Inspecao(){

    }

    public Inspecao(int id, String dataInicio, String dataFim, boolean isFinished, int inspetorId, int ObraId, boolean isActive) {
        Id = id;
        DataInicio = dataInicio;
        DataFim = dataFim;
        IsFinished = isFinished;
        InspetorId = inspetorId;
        this.ObraId = ObraId;
        IsActive = isActive;
    }

    public Inspecao(String dataInicio, String dataFim, boolean isFinished, int inspetorId, int ObraId, boolean isActive) {
        DataInicio = dataInicio;
        DataFim = dataFim;
        IsFinished = isFinished;
        InspetorId = inspetorId;
        this.ObraId = ObraId;
        IsActive = isActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDataInicio() {
        return DataInicio;
    }

    public void setDataInicio(String dataInicio) {
        DataInicio = dataInicio;
    }

    public String getDataFim() {
        return DataFim;
    }

    public void setDataFim(String dataFim) {
        DataFim = dataFim;
    }

    public boolean isFinished() {
        return IsFinished;
    }

    public void setFinished(boolean finished) {
        IsFinished = finished;
    }

    public int getInspetorId() {
        return InspetorId;
    }

    public void setInspetorId(int inspetorId) {
        InspetorId = inspetorId;
    }

    public int getObraId() {
        return ObraId;
    }

    public void setObraId(int ObraId) {
        this.ObraId = ObraId;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
