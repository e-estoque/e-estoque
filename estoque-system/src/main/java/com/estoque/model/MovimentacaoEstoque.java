package com.estoque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma movimentação de estoque: ENTRADA (compra) ou SAIDA (venda).
 */
public class MovimentacaoEstoque {

    public enum Tipo { ENTRADA, SAIDA }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private int produtoId;
    private String nomeProduto;      // campo auxiliar para exibição
    private Tipo tipo;
    private int quantidade;
    private BigDecimal valorUnitario;
    private String observacao;
    private LocalDateTime dataMovimentacao;

    public MovimentacaoEstoque() {
        this.dataMovimentacao = LocalDateTime.now();
    }

    // ------------------------------------------------------------------
    // Cálculo auxiliar
    // ------------------------------------------------------------------
    public BigDecimal getValorTotal() {
        if (valorUnitario == null) return BigDecimal.ZERO;
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    // ------------------------------------------------------------------
    // Getters e Setters
    // ------------------------------------------------------------------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva.");
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) {
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Valor unitário inválido.");
        this.valorUnitario = valorUnitario;
    }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public LocalDateTime getDataMovimentacao() { return dataMovimentacao; }
    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %-5d | %s | %-6s | Qtd: %-5d | Unit: R$ %8.2f | Total: R$ %9.2f | Data: %s | Obs: %s",
            id,
            tipo == Tipo.ENTRADA ? "[ENTRADA]" : "[ SAIDA ]",
            nomeProduto == null ? "ID:" + produtoId : nomeProduto.length() > 20
                ? nomeProduto.substring(0, 20) : nomeProduto,
            quantidade, valorUnitario, getValorTotal(),
            dataMovimentacao != null ? dataMovimentacao.format(FMT) : "-",
            observacao == null ? "-" : observacao
        );
    }
}
