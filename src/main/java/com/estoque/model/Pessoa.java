package com.estoque.model;

/**
 * Classe abstrata que representa uma Pessoa genérica.
 * Demonstra herança: Fornecedor e Cliente estendem esta classe.
 */
public abstract class Pessoa {

    private String nome;
    private String contato;

    // Composição: endereço encapsulado em objeto próprio
    private Endereco endereco;

    public Pessoa() {}

    public Pessoa(String nome, String contato) {
        this.nome    = nome;
        this.contato = contato;
    }

    // ------------------------------------------------------------------
    // Método abstrato: cada subclasse define sua apresentação
    // ------------------------------------------------------------------
    public abstract String getTipoPessoa();

    // ------------------------------------------------------------------
    // Getters e Setters (Encapsulamento)
    // ------------------------------------------------------------------
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
