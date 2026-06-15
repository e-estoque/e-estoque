package com.estoque.model;

public abstract class Pessoa {

    private String nome;
    private String contato;
    private Endereco endereco;

    public Pessoa() {}

    public Pessoa(String nome, String contato) {
        this.nome    = nome;
        this.contato = contato;
    }

    public abstract String getTipoPessoa();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return getTipoPessoa() + " [nome=" + nome + ", contato=" + contato + "]";
    }
}


