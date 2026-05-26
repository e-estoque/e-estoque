package com.estoque.relatorio;

/**
 * Interface Relatorio - contrato para geração de relatórios.
 * Implementada por RelatorioEstoque, RelatorioGastosMensais e
 * RelatorioPrevisao.
 */
public interface Relatorio {

    /**
     * Gera e imprime o relatório no terminal.
     */
    void gerar();

    /**
     * Retorna o título do relatório para exibição nos menus.
     */
    String getTitulo();
}
