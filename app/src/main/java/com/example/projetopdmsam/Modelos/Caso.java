package com.example.projetopdmsam.Modelos;

public class Caso {

    int Id;
    String Titulo;
    String Descricao;
    String Imagem;
    int InspecaoId;

    public Caso(){

    }

    public Caso(int id, String titulo, String descricao, String imagem, int inspecaoId) {
        Id = id;
        Titulo = titulo;
        Descricao = descricao;
        Imagem = imagem;
        InspecaoId = inspecaoId;
    }

    public Caso(String titulo, String descricao, String imagem, int inspecaoId) {
        Titulo = titulo;
        Descricao = descricao;
        Imagem = imagem;
        InspecaoId = inspecaoId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public String getImagem() {
        return Imagem;
    }

    public void setImagem(String imagem) {
        Imagem = imagem;
    }

    public int getInspecaoId() {
        return InspecaoId;
    }

    public void setInspecaoId(int inspecaoId) {
        InspecaoId = inspecaoId;
    }
}
