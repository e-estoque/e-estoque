package com.estoque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovimentacaoEstoque {

    public enum Tipo { ENTRADA, SAIDA }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private int produtoId;
    private String nomeProduto; 
    private Tipo tipo;
    private int quantidade;
    private BigDecimal valorUnitario;
    private String observacao;
    private LocalDateTime dataMovimentacao;

    public MovimentacaoEstoque() {
        this.dataMovimentacao = LocalDateTime.now();
    }

    public BigDecimal getValorTotal() {
        if (valorUnitario == null) return BigDecimal.ZERO;
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public int getId()        { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId()             { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getNomeProduto()                { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Tipo getTipo()         { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) {
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor unitário inválido.");
        }
        this.valorUnitario = valorUnitario;
    }

    public String getObservacao()               { return observacao; }
    public void setObservacao(String observacao)  { this.observacao = observacao; }

    public LocalDateTime getDataMovimentacao()                      { return dataMovimentacao; }
    public void setDataMovimentacao(LocalDateTime dataMovimentacao)  { this.dataMovimentacao = dataMovimentacao; }

    @Override
    public String toString() {
        String nomeCurto;
        if (nomeProduto == null) {
            nomeCurto = "ID:" + produtoId;
        } else if (nomeProduto.length() > 20) {
            nomeCurto = nomeProduto.substring(0, 20);
        } else {
            nomeCurto = nomeProduto;
        }

        String tipoStr = tipo == Tipo.ENTRADA ? "[ENTRADA]" : "[ SAIDA ]";
        String dataStr = dataMovimentacao != null ? dataMovimentacao.format(FMT) : "-";
        String obsStr  = observacao == null ? "-" : observacao;

        return String.format(
            "ID: %-5d | %s | %-20s | Qtd: %-5d | Unit: R$ %8.2f | Total: R$ %9.2f | Data: %s | Obs: %s",
            id, tipoStr, nomeCurto, quantidade, valorUnitario, getValorTotal(), dataStr, obsStr
        );
    }
}
