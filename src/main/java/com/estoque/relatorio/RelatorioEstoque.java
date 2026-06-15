package com.estoque.relatorio;

import com.estoque.dao.ProdutoDAO;
import com.estoque.model.Produto;
import com.estoque.util.ConsoleUtil;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioEstoque implements Relatorio {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @Override
    public String getTitulo() {
        return "Relatório de Estoque Atual";
    }

    @Override
    public void gerar() {
        ConsoleUtil.titulo(getTitulo());

        List<Produto> produtos = produtoDAO.listarTodos();

        if (produtos.isEmpty()) {
            ConsoleUtil.aviso("Nenhum produto cadastrado.");
            return;
        }

        System.out.printf("  %-4s  %-28s  %-12s  %6s  %10s  %10s  %8s  %s%n",
                "ID", "Nome", "Categoria", "Qtd", "Compra", "Venda", "Margem%", "Alerta");
        ConsoleUtil.separador();

        BigDecimal totalCusto = BigDecimal.ZERO;
        BigDecimal totalVenda = BigDecimal.ZERO;
        int contAbaixoMin = 0;

        for (Produto p : produtos) {
            String alerta = p.estoqueAbaixoMinimo() ? "BAIXO" : "OK";
            String categoria = p.getCategoria() == null ? "-" : p.getCategoria();

            System.out.printf("  %-4d  %-28s  %-12s  %6d  R$%8.2f  R$%8.2f  %7.1f%%  %s%n",
                    p.getId(),
                    truncar(p.getNome(), 28),
                    truncar(categoria, 12),
                    p.getQuantidadeEstoque(),
                    p.getPrecoCompra(),
                    p.getPrecoVenda(),
                    p.getMargemLucroPercent(),
                    alerta);

            BigDecimal custoProd = p.getPrecoCompra().multiply(BigDecimal.valueOf(p.getQuantidadeEstoque()));
            BigDecimal vendaProd = p.getPrecoVenda().multiply(BigDecimal.valueOf(p.getQuantidadeEstoque()));

            totalCusto = totalCusto.add(custoProd);
            totalVenda = totalVenda.add(vendaProd);

            if (p.estoqueAbaixoMinimo()) {
                contAbaixoMin++;
            }
        }

        ConsoleUtil.separador();
        System.out.printf("  Total de produtos: %d  |  Abaixo do mínimo: %d%n",
                produtos.size(), contAbaixoMin);
        System.out.printf("  Valor de custo em estoque: R$ %,.2f%n", totalCusto);
        System.out.printf("  Valor de venda em estoque: R$ %,.2f%n", totalVenda);
        System.out.printf("  Lucro potencial (estoque): R$ %,.2f%n", totalVenda.subtract(totalCusto));
    }

    private String truncar(String s, int max) {
        if (s == null) return "-";
        if (s.length() > max) return s.substring(0, max - 1) + "…";
        return s;
    }
}
