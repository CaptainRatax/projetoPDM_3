package com.example.projetopdmam.Modelos;

public class Utilizador {

    int Id;
    String Nome;
    String Morada;
    String CodigoPostal;
    String Telemovel;
    String NIF;
    String Email;
    String Password;
    boolean IsActive;

    public Utilizador() {

    }

    public Utilizador(int id, String nome, String morada, String codigoPostal, String telemovel, String NIF, String email, String password, boolean isActive) {
        Id = id;
        Nome = nome;
        Morada = morada;
        CodigoPostal = codigoPostal;
        Telemovel = telemovel;
        this.NIF = NIF;
        Email = email;
        Password = password;
        IsActive = isActive;
    }

    public Utilizador(String nome, String morada, String codigoPostal, String telemovel, String NIF, String email, String password, boolean isActive) {
        Nome = nome;
        Morada = morada;
        CodigoPostal = codigoPostal;
        Telemovel = telemovel;
        this.NIF = NIF;
        Email = email;
        Password = password;
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

    public String getTelemovel() {
        return Telemovel;
    }

    public void setTelemovel(String telemovel) {
        Telemovel = telemovel;
    }

    public String getNIF() {
        return NIF;
    }

    public void setNIF(String NIF) {
        this.NIF = NIF;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
