package com.example.projetopdmam.Modelos;

public class Caso {

    int Id;
    int EstacionamentoId;
    String Titulo;
    String Descricao;
    String Fotografia;
    boolean isActive;


    public Caso(){

    }

    public Caso(int id, int estacionamentoId, String titulo, String descricao, String fotografia, boolean isActive) {
        Id = id;
        EstacionamentoId = estacionamentoId;
        Titulo = titulo;
        Descricao = descricao;
        Fotografia = fotografia;
        this.isActive = isActive;
    }

    public Caso(int estacionamentoId, String titulo, String descricao, String fotografia, boolean isActive) {
        EstacionamentoId = estacionamentoId;
        Titulo = titulo;
        Descricao = descricao;
        Fotografia = fotografia;
        this.isActive = isActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getEstacionamentoId() {
        return EstacionamentoId;
    }

    public void setEstacionamentoId(int estacionamentoId) {
        EstacionamentoId = estacionamentoId;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getFotografia() {
        return Fotografia;
    }

    public void setFotografia(String fotografia) {
        Fotografia = fotografia;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
