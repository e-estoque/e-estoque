package com.estoque.model;

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

    @Override
    public String getTipoPessoa() {
        return "FORNECEDOR";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCep() {
        if (getEndereco() != null) return getEndereco().getCep();
        return "";
    }

    public String getLogradouro() {
        if (getEndereco() != null) return getEndereco().getLogradouro();
        return "";
    }

    public String getNumero() {
        if (getEndereco() != null) return getEndereco().getNumero();
        return "";
    }

    public String getComplemento() {
        if (getEndereco() != null) return getEndereco().getComplemento();
        return "";
    }

    public String getBairro() {
        if (getEndereco() != null) return getEndereco().getBairro();
        return "";
    }

    public String getCidade() {
        if (getEndereco() != null) return getEndereco().getCidade();
        return "";
    }

    public String getUf() {
        if (getEndereco() != null) return getEndereco().getUf();
        return "";
    }

    @Override
    public String toString() {
        String contatoStr = getContato() == null ? "-" : getContato();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ID: %-4d | %-30s | Contato: %-20s", id, getNome(), contatoStr));
        if (getEndereco() != null) {
            sb.append("\n         Endereço: ").append(getEndereco());
        }
        return sb.toString();
    }
}
