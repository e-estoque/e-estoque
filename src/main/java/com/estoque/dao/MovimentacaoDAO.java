package com.estoque.dao;

import com.estoque.model.MovimentacaoEstoque;
import com.estoque.model.Produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class MovimentacaoDAO {

    public int registrar(MovimentacaoEstoque mov) {
        Produto p = Dados.produtos.get(mov.getProdutoId());
        if (p == null) {
            throw new IllegalStateException("Produto ID " + mov.getProdutoId() + " não encontrado.");
        }

        int estoqueAtual = p.getQuantidadeEstoque();
        int novaQtd;

        if (mov.getTipo() == MovimentacaoEstoque.Tipo.ENTRADA) {
            novaQtd = estoqueAtual + mov.getQuantidade();
        } else { // SAIDA
            if (mov.getQuantidade() > estoqueAtual) {
                throw new IllegalStateException(
                    "Estoque insuficiente. Disponível: " + estoqueAtual +
                    " | Solicitado: " + mov.getQuantidade());
            }
            novaQtd = estoqueAtual - mov.getQuantidade();
        }

        int id = Dados.nextMovimentacaoId();
        mov.setId(id);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setNomeProduto(p.getNome());
        Dados.movimentacoes.add(mov);
        p.setQuantidadeEstoque(novaQtd);
        return id;
    }

    public List<MovimentacaoEstoque> listarTodas() {
        List<MovimentacaoEstoque> lista = new ArrayList<>(Dados.movimentacoes);

        for (MovimentacaoEstoque m : lista) {
            preencherNomeProduto(m);
        }

        lista.sort((a, b) -> b.getDataMovimentacao().compareTo(a.getDataMovimentacao()));
        return lista;
    }

    public List<MovimentacaoEstoque> listarPorProduto(int produtoId) {
        List<MovimentacaoEstoque> lista = new ArrayList<>();

        for (MovimentacaoEstoque m : Dados.movimentacoes) {
            if (m.getProdutoId() == produtoId) {
                preencherNomeProduto(m);
                lista.add(m);
            }
        }

        lista.sort((a, b) -> b.getDataMovimentacao().compareTo(a.getDataMovimentacao()));
        return lista;
    }

    public List<Object[]> gastosMensais(int meses) {
        return agruparPorMes(MovimentacaoEstoque.Tipo.ENTRADA, LocalDateTime.now().minusMonths(meses));
    }

    public List<Object[]> vendasMensais(int meses) {
        return agruparPorMes(MovimentacaoEstoque.Tipo.SAIDA, LocalDateTime.now().minusMonths(meses));
    }

    public BigDecimal totalVendasMesAtual() {
        return somarMesAtual(MovimentacaoEstoque.Tipo.SAIDA);
    }

    public BigDecimal totalGastosMesAtual() {
        return somarMesAtual(MovimentacaoEstoque.Tipo.ENTRADA);
    }

    public BigDecimal mediaVendasMensaisUltimos(int meses) {
        List<Object[]> vendas = vendasMensais(meses);
        if (vendas.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal soma = BigDecimal.ZERO;
        for (Object[] row : vendas) {
            soma = soma.add((BigDecimal) row[2]);
        }

        return soma.divide(BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP);
    }

    // Agrupa movimentações por mês, retornando lista com [ano, mês, total, quantidade]
    private List<Object[]> agruparPorMes(MovimentacaoEstoque.Tipo tipo, LocalDateTime limite) {
        Map<YearMonth, Object[]> mapa = new TreeMap<>(Comparator.reverseOrder());

        for (MovimentacaoEstoque m : Dados.movimentacoes) {
            if (m.getTipo() != tipo) continue;
            if (m.getDataMovimentacao() == null) continue;
            if (!m.getDataMovimentacao().isAfter(limite)) continue;

            YearMonth ym = YearMonth.from(m.getDataMovimentacao());

            if (!mapa.containsKey(ym)) {
                mapa.put(ym, new Object[]{ym.getYear(), ym.getMonthValue(), BigDecimal.ZERO, 0});
            }

            Object[] row = mapa.get(ym);
            row[2] = ((BigDecimal) row[2]).add(m.getValorTotal());
            row[3] = (int) row[3] + 1;
        }

        return new ArrayList<>(mapa.values());
    }

    private BigDecimal somarMesAtual(MovimentacaoEstoque.Tipo tipo) {
        YearMonth mesAtual = YearMonth.now();
        BigDecimal total = BigDecimal.ZERO;

        for (MovimentacaoEstoque m : Dados.movimentacoes) {
            if (m.getTipo() == tipo && m.getDataMovimentacao() != null) {
                YearMonth mesMov = YearMonth.from(m.getDataMovimentacao());
                if (mesMov.equals(mesAtual)) {
                    total = total.add(m.getValorTotal());
                }
            }
        }

        return total;
    }

    private void preencherNomeProduto(MovimentacaoEstoque m) {
        if (m.getNomeProduto() == null) {
            Produto p = Dados.produtos.get(m.getProdutoId());
            if (p != null) {
                m.setNomeProduto(p.getNome());
            }
        }
    }
}
