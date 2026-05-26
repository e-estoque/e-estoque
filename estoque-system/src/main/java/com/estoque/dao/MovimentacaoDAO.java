package com.estoque.dao;

import com.estoque.model.MovimentacaoEstoque;
import com.estoque.model.Produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class MovimentacaoDAO {

    // ------------------------------------------------------------------
    // CREATE - atômico via synchronized
    // ------------------------------------------------------------------
    public synchronized int registrar(MovimentacaoEstoque mov) {
        Produto p = Dados.produtos.get(mov.getProdutoId());
        if (p == null) {
            throw new IllegalStateException("Produto ID " + mov.getProdutoId() + " não encontrado.");
        }

        int estoqueAtual = p.getQuantidadeEstoque();
        int novaQtd;

        switch (mov.getTipo()) {
            case ENTRADA -> novaQtd = estoqueAtual + mov.getQuantidade();
            case SAIDA -> {
                if (mov.getQuantidade() > estoqueAtual) {
                    throw new IllegalStateException(
                            "Estoque insuficiente. Disponível: " + estoqueAtual +
                                    " | Solicitado: " + mov.getQuantidade());
                }
                novaQtd = estoqueAtual - mov.getQuantidade();
            }
            default -> throw new IllegalStateException("Tipo de movimentação desconhecido.");
        }

        int id = Dados.nextMovimentacaoId();
        mov.setId(id);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setNomeProduto(p.getNome());
        Dados.movimentacoes.add(mov);
        p.setQuantidadeEstoque(novaQtd);
        return id;
    }

    // ------------------------------------------------------------------
    // READ - listar todas
    // ------------------------------------------------------------------
    public List<MovimentacaoEstoque> listarTodas() {
        return Dados.movimentacoes.stream()
                .sorted(Comparator.comparing(MovimentacaoEstoque::getDataMovimentacao).reversed())
                .map(this::comNomeProduto)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // READ - por produto
    // ------------------------------------------------------------------
    public List<MovimentacaoEstoque> listarPorProduto(int produtoId) {
        return Dados.movimentacoes.stream()
                .filter(m -> m.getProdutoId() == produtoId)
                .sorted(Comparator.comparing(MovimentacaoEstoque::getDataMovimentacao).reversed())
                .map(this::comNomeProduto)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // READ - gastos mensais (entradas)
    // ------------------------------------------------------------------
    public List<Object[]> gastosMensais(int meses) {
        return agruparPorMes(MovimentacaoEstoque.Tipo.ENTRADA,
                LocalDateTime.now().minusMonths(meses));
    }

    // ------------------------------------------------------------------
    // READ - vendas mensais (saídas)
    // ------------------------------------------------------------------
    public List<Object[]> vendasMensais(int meses) {
        return agruparPorMes(MovimentacaoEstoque.Tipo.SAIDA,
                LocalDateTime.now().minusMonths(meses));
    }

    // ------------------------------------------------------------------
    // READ - totais do mês atual
    // ------------------------------------------------------------------
    public BigDecimal totalVendasMesAtual() {
        return somarMesAtual(MovimentacaoEstoque.Tipo.SAIDA);
    }

    public BigDecimal totalGastosMesAtual() {
        return somarMesAtual(MovimentacaoEstoque.Tipo.ENTRADA);
    }

    // ------------------------------------------------------------------
    // READ - média de vendas dos últimos N meses
    // ------------------------------------------------------------------
    public BigDecimal mediaVendasMensaisUltimos(int meses) {
        List<Object[]> vendas = vendasMensais(meses);
        if (vendas.isEmpty())
            return BigDecimal.ZERO;
        BigDecimal soma = vendas.stream()
                .map(row -> (BigDecimal) row[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private List<Object[]> agruparPorMes(MovimentacaoEstoque.Tipo tipo, LocalDateTime limite) {
        // TreeMap com ordem decrescente de YearMonth
        Map<YearMonth, Object[]> mapa = new TreeMap<>(Comparator.reverseOrder());

        Dados.movimentacoes.stream()
                .filter(m -> m.getTipo() == tipo
                        && m.getDataMovimentacao() != null
                        && m.getDataMovimentacao().isAfter(limite))
                .forEach(m -> {
                    YearMonth ym = YearMonth.from(m.getDataMovimentacao());
                    Object[] row = mapa.computeIfAbsent(ym,
                            k -> new Object[] { ym.getYear(), ym.getMonthValue(), BigDecimal.ZERO, 0 });
                    row[2] = ((BigDecimal) row[2]).add(m.getValorTotal());
                    row[3] = (int) row[3] + 1;
                });

        return new ArrayList<>(mapa.values());
    }

    private BigDecimal somarMesAtual(MovimentacaoEstoque.Tipo tipo) {
        YearMonth atual = YearMonth.now();
        return Dados.movimentacoes.stream()
                .filter(m -> m.getTipo() == tipo
                        && m.getDataMovimentacao() != null
                        && YearMonth.from(m.getDataMovimentacao()).equals(atual))
                .map(MovimentacaoEstoque::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private MovimentacaoEstoque comNomeProduto(MovimentacaoEstoque m) {
        if (m.getNomeProduto() == null) {
            Produto p = Dados.produtos.get(m.getProdutoId());
            if (p != null)
                m.setNomeProduto(p.getNome());
        }
        return m;
    }
}
