package com.estoque.model;

import java.math.BigDecimal;

/**
 * Representa um produto do estoque.
 * Todos os atributos são privados (encapsulamento).
 */
public class Produto {

    private int id;
    private String nome;
    private String descricao;
    private BigDecimal precoCompra;
    private BigDecimal precoVenda;
    private int quantidadeEstoque;
    private int estoqueMinimo;
    private String categoria;
    private int fornecedorId;
    private String nomeFornecedor;   // campo auxiliar para exibição (JOIN)
    private boolean ativo;

    public Produto() {
        this.ativo = true;
        this.estoqueMinimo = 10;
    }

    // ------------------------------------------------------------------
    // Getters e Setters (Encapsulamento)
    // ------------------------------------------------------------------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do produto não pode ser vazio.");
        this.nome = nome.trim();
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoCompra() { return precoCompra; }
    public void setPrecoCompra(BigDecimal precoCompra) {
        if (precoCompra == null || precoCompra.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço de compra inválido.");
        this.precoCompra = precoCompra;
    }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) {
        if (precoVenda == null || precoVenda.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Preço de venda inválido.");
        this.precoVenda = precoVenda;
    }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }

    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    /**
     * Calcula a margem de lucro em percentual.
     */
    public double getMargemLucroPercent() {
        if (precoCompra == null || precoCompra.compareTo(BigDecimal.ZERO) == 0)
            return 0.0;
        return precoVenda.subtract(precoCompra)
                         .divide(precoCompra, 4, java.math.RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100))
                         .doubleValue();
    }

    /**
     * Retorna true se o estoque estiver abaixo do mínimo.
     */
    public boolean estoqueAbaixoMinimo() {
        return quantidadeEstoque < estoqueMinimo;
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %-4d | %-30s | Cat: %-15s | Qtd: %-5d | Compra: R$ %8.2f | Venda: R$ %8.2f | Fornecedor: %s",
            id, nome, categoria == null ? "-" : categoria,
            quantidadeEstoque, precoCompra, precoVenda,
            nomeFornecedor == null ? "-" : nomeFornecedor
        );
    }
}
