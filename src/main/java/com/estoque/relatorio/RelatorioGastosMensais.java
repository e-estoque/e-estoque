package com.estoque.relatorio;

import com.estoque.dao.MovimentacaoDAO;
import com.estoque.util.ConsoleUtil;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioGastosMensais implements Relatorio {

    private static final String[] MESES = {
            "", "JAN", "FEV", "MAR", "ABR", "MAI", "JUN",
            "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"
    };

    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

    @Override
    public String getTitulo() {
        return "Relatório de Gastos e Vendas Mensais";
    }

    @Override
    public void gerar() {
        ConsoleUtil.titulo(getTitulo());

        List<Object[]> gastos = movimentacaoDAO.gastosMensais(12);
        List<Object[]> vendas = movimentacaoDAO.vendasMensais(12);

        if (gastos.isEmpty() && vendas.isEmpty()) {
            ConsoleUtil.aviso("Nenhuma movimentação encontrada nos últimos 12 meses.");
            return;
        }

        ConsoleUtil.subtitulo("Gastos com Compras (Entradas) - últimos 12 meses");
        System.out.printf("  %-8s  %15s  %12s%n", "Período", "Total Gasto", "Nº Compras");
        ConsoleUtil.separador();

        BigDecimal totalGeral = BigDecimal.ZERO;

        for (Object[] row : gastos) {
            int ano       = (int) row[0];
            int mes       = (int) row[1];
            BigDecimal total = (BigDecimal) row[2];
            int numCompras   = (int) row[3];

            System.out.printf("  %s/%d  R$ %,12.2f  %12d%n", MESES[mes], ano, total, numCompras);
            totalGeral = totalGeral.add(total);
        }

        ConsoleUtil.separador();
        System.out.printf("  Total geral (12 meses):   R$ %,12.2f%n", totalGeral);

        ConsoleUtil.subtitulo("Vendas (Saídas) - últimos 12 meses");
        System.out.printf("  %-8s  %15s  %12s%n", "Período", "Total Vendas", "Nº Vendas");
        ConsoleUtil.separador();

        BigDecimal totalVendas = BigDecimal.ZERO;

        for (Object[] row : vendas) {
            int ano       = (int) row[0];
            int mes       = (int) row[1];
            BigDecimal total = (BigDecimal) row[2];
            int numVendas    = (int) row[3];

            System.out.printf("  %s/%d  R$ %,12.2f  %12d%n", MESES[mes], ano, total, numVendas);
            totalVendas = totalVendas.add(total);
        }

        ConsoleUtil.separador();
        System.out.printf("  Total vendas (12 meses):  R$ %,12.2f%n", totalVendas);

        ConsoleUtil.subtitulo("Lucro Bruto Estimado (12 meses)");
        BigDecimal lucroBruto = totalVendas.subtract(totalGeral);
        System.out.printf("  Receita total de vendas:  R$ %,12.2f%n", totalVendas);
        System.out.printf("  Custo total de compras:   R$ %,12.2f%n", totalGeral);
        System.out.printf("  Lucro bruto:              R$ %,12.2f%n", lucroBruto);

        if (lucroBruto.compareTo(BigDecimal.ZERO) >= 0) {
            ConsoleUtil.sucesso("Operação no lucro!");
        } else {
            ConsoleUtil.aviso("Operação no prejuízo no período.");
        }
    }
}
