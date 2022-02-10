package com.example.projetopdmam.Modelos;

public class Lugar {
    int Id;
    String Codigo;
    String Andar;
    boolean IsActive;

    public Lugar() {

    }

    public Lugar(int id, String codigo, String andar, boolean isActive) {
        Id = id;
        Codigo = codigo;
        Andar = andar;
        IsActive = isActive;
    }

    public Lugar(String codigo, String andar, boolean isActive) {
        Codigo = codigo;
        Andar = andar;
        IsActive = isActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getAndar() {
        return Andar;
    }

    public void setAndar(String andar) {
        Andar = andar;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
