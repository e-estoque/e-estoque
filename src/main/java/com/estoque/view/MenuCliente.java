package com.estoque.view;

import com.estoque.dao.Dados;
import com.estoque.dao.ProdutoDAO;
import com.estoque.model.Produto;
import com.estoque.util.ConsoleUtil;

import java.util.List;

/**
 * Menu do Cliente - acesso público, sem login.
 * Permite consultar produtos disponíveis e ver detalhes.
 */
public class MenuCliente {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    public void iniciar() {
        boolean ativo = true;

        while (ativo) {
            ConsoleUtil.titulo("Área do Cliente");
            ConsoleUtil.linha("[1] Ver todos os produtos disponíveis");
            ConsoleUtil.linha("[2] Buscar produto por nome");
            ConsoleUtil.linha("[3] Ver detalhes de um produto");
            ConsoleUtil.linha("[0] Voltar ao Menu Principal");
            System.out.println();

            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, 3);

            switch (opcao) {
                case 1 -> listarProdutosDisponiveis();
                case 2 -> buscarPorNome();
                case 3 -> verDetalhes();
                case 0 -> ativo = false;
            }

            if (ativo)
                ConsoleUtil.pausar();
        }

        // Mensagem de atendimento exibida ao sair da área do cliente
        exibirContatoVendedor();
    }

    // ------------------------------------------------------------------
    private void listarProdutosDisponiveis() {
        ConsoleUtil.subtitulo("Produtos Disponíveis");
        List<Produto> produtos = produtoDAO.listarTodos();

        if (produtos.isEmpty()) {
            ConsoleUtil.aviso("Nenhum produto disponível no momento.");
            return;
        }

        System.out.printf("  %-4s  %-35s  %-15s  %10s  %8s%n",
                "ID", "Nome", "Categoria", "Preço", "Estoque");
        ConsoleUtil.separador();

        // Laço for com continue: pula produtos sem estoque na listagem do cliente
        for (Produto p : produtos) {
            if (p.getQuantidadeEstoque() == 0) {
                continue; // continue: produto sem estoque não é exibido ao cliente
            }

            System.out.printf("  %-4d  %-35s  %-15s  R$ %7.2f  %8d un.%n",
                    p.getId(),
                    truncar(p.getNome(), 35),
                    truncar(p.getCategoria() == null ? "-" : p.getCategoria(), 15),
                    p.getPrecoVenda(),
                    p.getQuantidadeEstoque());
        }
    }

    private void buscarPorNome() {
        String nome = ConsoleUtil.lerString("  Nome do produto (parcial): ");
        List<Produto> resultado = produtoDAO.buscarPorNome(nome);
        if (resultado.isEmpty()) {
            ConsoleUtil.aviso("Nenhum produto encontrado para: \"" + nome + "\"");
            return;
        }

        boolean algumDisponivel = false;
        for (Produto p : resultado) {
            if (p.getQuantidadeEstoque() > 0) {
                algumDisponivel = true;
                System.out.printf("  ID: %-4d | %-35s | R$ %.2f | Estoque: %d un.%n",
                        p.getId(), p.getNome(), p.getPrecoVenda(), p.getQuantidadeEstoque());
            }
        }

        if (!algumDisponivel) {
            ConsoleUtil.aviso("Produtos encontrados, mas sem estoque disponível.");
        }
    }

    private void verDetalhes() {
        int id = ConsoleUtil.lerInt("  ID do produto: ");
        Produto p = produtoDAO.buscarPorId(id);

        if (p == null) {
            ConsoleUtil.aviso("Produto ID " + id + " não encontrado.");
            return;
        }

        ConsoleUtil.subtitulo("Detalhes do Produto");
        System.out.println();
        System.out.printf("  Nome        : %s%n", p.getNome());
        System.out.printf("  Descrição   : %s%n",
                p.getDescricao() == null || p.getDescricao().isBlank() ? "Não informado" : p.getDescricao());
        System.out.printf("  Categoria   : %s%n",
                p.getCategoria() == null ? "Não categorizado" : p.getCategoria());
        System.out.printf("  Preço       : R$ %.2f%n", p.getPrecoVenda());
        System.out.printf("  Fornecedor  : %s%n",
                p.getNomeFornecedor() == null ? "Não informado" : p.getNomeFornecedor());

        // if/else if/else para status do estoque
        if (p.getQuantidadeEstoque() == 0) {
            ConsoleUtil.aviso("INDISPONÍVEL - Produto sem estoque no momento.");
        } else if (p.getQuantidadeEstoque() < 5) {
            System.out.printf("  Disponível  : %d unidade(s) - ÚLTIMAS UNIDADES!%n",
                    p.getQuantidadeEstoque());
        } else {
            System.out.printf("  Disponível  : %d unidade(s) em estoque%n",
                    p.getQuantidadeEstoque());
        }
    }

    /** Exibe contato do vendedor ao encerrar a sessão do cliente. */
    private void exibirContatoVendedor() {
        String whatsapp = Dados.getPropriedade("loja.whatsapp");
        String nomeLoja = Dados.getPropriedade("loja.nome");

        System.out.println();
        ConsoleUtil.separadorDuplo();
        ConsoleUtil.linha("Obrigado por visitar " + (nomeLoja.isBlank() ? "nossa loja" : nomeLoja) + "!");
        ConsoleUtil.linha("Para atendimento personalizado, entre em contato pelo WhatsApp:");
        ConsoleUtil.linha("");
        System.out.println("     WhatsApp: " + (whatsapp.isBlank() ? "(11) 91234-5678" : whatsapp));
        ConsoleUtil.linha("");
        ConsoleUtil.linha("Será um prazer atendê-lo!");
        ConsoleUtil.separadorDuplo();
        System.out.println();
    }

    private String truncar(String s, int max) {
        if (s == null)
            return "-";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}
