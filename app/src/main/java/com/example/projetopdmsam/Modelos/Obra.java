package com.example.projetopdmsam.Modelos;

public class Obra {
    int Id;
    String Nome;
    String Descricao;
    String Morada;
    String CodigoPostal;
    String Localidade;
    String Pais;
    String DataInicio;
    String Responsavel;
    boolean IsActive;

    public Obra() {

    }

    public Obra(int id, String nome, String descricao, String morada, String codigoPostal, String localidade, String pais, String dataInicio, String responsavel, boolean isActive) {
        Id = id;
        Nome = nome;
        Descricao = descricao;
        Morada = morada;
        CodigoPostal = codigoPostal;
        Localidade = localidade;
        Pais = pais;
        DataInicio = dataInicio;
        Responsavel = responsavel;
        IsActive = isActive;
    }

    public Obra(String nome, String descricao, String morada, String codigoPostal, String localidade, String pais, String dataInicio, String responsavel, boolean isActive) {
        Nome = nome;
        Descricao = descricao;
        Morada = morada;
        CodigoPostal = codigoPostal;
        Localidade = localidade;
        Pais = pais;
        DataInicio = dataInicio;
        Responsavel = responsavel;
        IsActive = isActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getMorada() {
        return Morada;
    }

    public void setMorada(String morada) {
        Morada = morada;
    }

    public String getCodigoPostal() {
        return CodigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        CodigoPostal = codigoPostal;
    }

    public String getLocalidade() {
        return Localidade;
    }

    public void setLocalidade(String localidade) {
        Localidade = localidade;
    }

    public String getPais() {
        return Pais;
    }

    public void setPais(String pais) {
        Pais = pais;
    }

    public String getDataInicio() {
        return DataInicio;
    }

    public void setDataInicio(String dataInicio) {
        DataInicio = dataInicio;
    }

    public String getResponsavel() {
        return Responsavel;
    }

    public void setResponsavel(String responsavel) {
        Responsavel = responsavel;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
