package com.estoque.model;

/**
 * Cliente - estende Pessoa (herança).
 * No fluxo atual, o cliente não faz login nem cadastro;
 * esta classe demonstra o uso de herança e pode ser expandida.
 */
public class Cliente extends Pessoa {

    private int id;

    public Cliente() {
        super();
    }

    public Cliente(String nome, String contato) {
        super(nome, contato);
    }

    @Override
    public String getTipoPessoa() {
        return "CLIENTE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
