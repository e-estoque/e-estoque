package com.estoque.model;

/**
 * Usuário do sistema - apenas perfil LOJISTA nesta versão.
 */
public class Usuario {

    private int id;
    private String login;
    private String senha;
    private String perfil;

    public Usuario() {
    }

    public Usuario(int id, String login, String perfil) {
        this.id = id;
        this.login = login;
        this.perfil = perfil;
    }

    // ------------------------------------------------------------------
    // Getters e Setters
    // ------------------------------------------------------------------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return "Usuario [login=" + login + ", perfil=" + perfil + "]";
    }
}
