package com.estoque.model;

/**
 * Fornecedor - estende Pessoa (herança).
 * Possui endereço preenchível via ViaCEP.
 */
public class Fornecedor extends Pessoa {

    private int id;

    public Fornecedor() {
        super();
    }

    public Fornecedor(String nome, String contato) {
        super(nome, contato);
    }

    public Fornecedor(int id, String nome, String contato) {
        super(nome, contato);
        this.id = id;
    }

    // ------------------------------------------------------------------
    // Implementação do método abstrato
    // ------------------------------------------------------------------
    @Override
    public String getTipoPessoa() {
        return "FORNECEDOR";
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

    // Atalhos para campos do Endereco (composição)
    public String getCep() {
        return getEndereco() != null ? getEndereco().getCep() : "";
    }

    public String getLogradouro() {
        return getEndereco() != null ? getEndereco().getLogradouro() : "";
    }

    public String getNumero() {
        return getEndereco() != null ? getEndereco().getNumero() : "";
    }

    public String getComplemento() {
        return getEndereco() != null ? getEndereco().getComplemento() : "";
    }

    public String getBairro() {
        return getEndereco() != null ? getEndereco().getBairro() : "";
    }

    public String getCidade() {
        return getEndereco() != null ? getEndereco().getCidade() : "";
    }

    public String getUf() {
        return getEndereco() != null ? getEndereco().getUf() : "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ID: %-4d | %-30s | Contato: %-20s",
                id, getNome(), getContato() == null ? "-" : getContato()));
        if (getEndereco() != null) {
            sb.append("\n         Endereço: ").append(getEndereco());
        }
        return sb.toString();
    }
}
