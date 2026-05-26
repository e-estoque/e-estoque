package com.estoque.model;

/**
 * Representa o endereço de uma Pessoa (composição).
 * Preenchido automaticamente via API ViaCEP.
 */
public class Endereco {

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;

    public Endereco() {}

    public Endereco(String cep, String logradouro, String bairro,
                    String cidade, String uf) {
        this.cep        = cep;
        this.logradouro = logradouro;
        this.bairro     = bairro;
        this.cidade     = cidade;
        this.uf         = uf;
    }

    // ------------------------------------------------------------------
    // Getters e Setters
    // ------------------------------------------------------------------
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    @Override
    public String toString() {
        return logradouro + ", " + numero +
               (complemento != null && !complemento.isBlank() ? " - " + complemento : "") +
               " | " + bairro + " | " + cidade + "/" + uf +
               " | CEP: " + cep;
    }
}
