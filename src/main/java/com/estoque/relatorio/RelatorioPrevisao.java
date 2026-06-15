package com.estoque.relatorio;

import com.estoque.dao.MovimentacaoDAO;
import com.estoque.dao.ProdutoDAO;
import com.estoque.model.Produto;
import com.estoque.util.ConsoleUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RelatorioPrevisao implements Relatorio {

    // Quantas vezes o estoque mínimo devemos repor quando o produto está em baixa
    private static final int MULTIPLICADOR_REPOSICAO = 5;
    private static final int MESES_HISTORICO = 3;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

    @Override
    public String getTitulo() {
        return "Relatório de Previsão de Gastos Futuros";
    }

    @Override
    public void gerar() {
        ConsoleUtil.titulo(getTitulo());

        BigDecimal mediaVendas = movimentacaoDAO
                .mediaVendasMensaisUltimos(MESES_HISTORICO)
                .setScale(2, RoundingMode.HALF_UP);

        ConsoleUtil.subtitulo("Base histórica (últimos " + MESES_HISTORICO + " meses)");
        System.out.printf("  Média de receita mensal:  R$ %,.2f%n", mediaVendas);

        // Estimativa de compras = 60% da receita (margem típica de 40%)
        BigDecimal estimativaComprasBasica = mediaVendas
                .multiply(BigDecimal.valueOf(0.60))
                .setScale(2, RoundingMode.HALF_UP);
        System.out.printf("  Estimativa base (60%%):   R$ %,.2f%n", estimativaComprasBasica);

        List<Produto> abaixoMinimo = produtoDAO.listarAbaixoEstoqueMinimo();

        ConsoleUtil.subtitulo("Reposições Urgentes - Estoque Abaixo do Mínimo");

        if (abaixoMinimo.isEmpty()) {
            ConsoleUtil.sucesso("Todos os produtos estão com estoque adequado.");
        } else {
            System.out.printf("  %-28s  %6s  %6s  %10s  %12s%n",
                    "Produto", "Atual", "Mín", "Repor", "Custo Est.");
            ConsoleUtil.separador();

            BigDecimal totalReposicao = BigDecimal.ZERO;

            for (Produto p : abaixoMinimo) {
                int qtdRepor = (p.getEstoqueMinimo() * MULTIPLICADOR_REPOSICAO) - p.getQuantidadeEstoque();
                if (qtdRepor < 0) {
                    qtdRepor = 0;
                }

                BigDecimal custoEst = p.getPrecoCompra()
                        .multiply(BigDecimal.valueOf(qtdRepor))
                        .setScale(2, RoundingMode.HALF_UP);

                System.out.printf("  %-28s  %6d  %6d  %10d  R$ %9.2f%n",
                        truncar(p.getNome(), 28),
                        p.getQuantidadeEstoque(),
                        p.getEstoqueMinimo(),
                        qtdRepor,
                        custoEst);

                totalReposicao = totalReposicao.add(custoEst);
            }

            ConsoleUtil.separador();
            System.out.printf("  Total estimado para reposição urgente: R$ %,.2f%n", totalReposicao);

            ConsoleUtil.subtitulo("Previsão Total de Gastos - Próximo Mês");
            BigDecimal previsaoTotal = estimativaComprasBasica.add(totalReposicao);

            System.out.printf("  Compras de rotina (estimativa):   R$ %,.2f%n", estimativaComprasBasica);
            System.out.printf("  Reposições urgentes:               R$ %,.2f%n", totalReposicao);
            System.out.printf("  PREVISÃO TOTAL:                    R$ %,.2f%n", previsaoTotal);
        }
    }

    private String truncar(String s, int max) {
        if (s == null) return "-";
        if (s.length() > max) return s.substring(0, max - 1) + "…";
        return s;
    }
}
