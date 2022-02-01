package com.example.projetopdmsam.Modelos;

public class Utilizador {

    int Id;
    String Nome;
    String Username;
    String Password;
    String Email;
    String Telemovel;
    boolean IsActive;

    public Utilizador() {

    }

    public Utilizador(int id, String nome, String username, String password, String email, String telemovel, boolean isActive) {
        Id = id;
        Nome = nome;
        Username = username;
        Password = password;
        Email = email;
        Telemovel = telemovel;
        IsActive = isActive;
    }

    public Utilizador(String nome, String username, String password, String email, String telemovel, boolean isActive) {
        Nome = nome;
        Username = username;
        Password = password;
        Email = email;
        Telemovel = telemovel;
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

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTelemovel() {
        return Telemovel;
    }

    public void setTelemovel(String telemovel) {
        Telemovel = telemovel;
    }

    public boolean isActive() {
        return IsActive;
    }

    public void setActive(boolean active) {
        IsActive = active;
    }
}
