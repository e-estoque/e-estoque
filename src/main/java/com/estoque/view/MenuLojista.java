package com.estoque.view;

import com.estoque.api.ViaCepClient;
import com.estoque.dao.*;
import com.estoque.model.*;
import com.estoque.relatorio.*;
import com.estoque.util.ConsoleUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Menu do Lojista - acesso administrativo completo.
 *
 * Requisitos demonstrados neste arquivo:
 * if/else if/else
 * switch/case
 * break, continue, return
 * try/catch/finally
 * while, for, do-while
 * Integração ViaCEP (diferencial)
 * CRUD completo de Produto, Fornecedor, Movimentação
 * Relatórios e Dashboard
 */
public class MenuLojista {

    // DAOs
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final FornecedorDAO fornecedorDAO = new FornecedorDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // API
    private final ViaCepClient viaCepClient = new ViaCepClient();

    // Relatórios
    private final Relatorio relEstoque = new RelatorioEstoque();
    private final Relatorio relGastos = new RelatorioGastosMensais();
    private final Relatorio relPrevisao = new RelatorioPrevisao();

    // ------------------------------------------------------------------
    // Fluxo principal
    // ------------------------------------------------------------------
    public void iniciar() {
        if (!autenticar()) {
            ConsoleUtil.aviso("Acesso negado. Retornando ao menu principal.");
            return; // return explícito (requisito obrigatório)
        }

        boolean ativo = true;

        while (ativo) {
            exibirMenuLojista();
            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, 7);

            switch (opcao) {
                case 1 -> menuProdutos();
                case 2 -> menuFornecedores();
                case 3 -> menuMovimentacoes();
                case 4 -> menuRelatorios();
                case 5 -> exibirDashboard();
                case 6 -> relEstoque.gerar();
                case 7 -> menuRelatorios();
                case 0 -> {
                    ConsoleUtil.info("Saindo do painel do lojista...");
                    ativo = false;
                }
                default -> ConsoleUtil.aviso("Opção inválida.");
            }

            if (ativo)
                ConsoleUtil.pausar();
        }
    }

    // ------------------------------------------------------------------
    // Autenticação
    // ------------------------------------------------------------------
    private boolean autenticar() {
        ConsoleUtil.titulo("Login - Área do Lojista");

        // do-while: tenta até 3 vezes (requisito do-while)
        int tentativas = 0;
        do {
            String login = ConsoleUtil.lerString("  Login: ");
            String senha = ConsoleUtil.lerString("  Senha: ");

            Usuario usuario = usuarioDAO.autenticar(login, senha);
            if (usuario != null) {
                ConsoleUtil.sucesso("Bem-vindo, " + usuario.getLogin() + "! Perfil: " + usuario.getPerfil());
                return true;
            }
            ConsoleUtil.erro("Login ou senha incorretos.");
            tentativas++;

            if (tentativas < 3) {
                ConsoleUtil.info("Tentativas restantes: " + (3 - tentativas));
            }

        } while (tentativas < 3);

        ConsoleUtil.erro("Número máximo de tentativas atingido.");
        return false;
    }

    // ------------------------------------------------------------------
    // Menus de nível 2
    // ------------------------------------------------------------------
    private void exibirMenuLojista() {
        ConsoleUtil.titulo("Painel do Lojista");
        ConsoleUtil.linha("[1] Gerenciar Produtos");
        ConsoleUtil.linha("[2] Gerenciar Fornecedores");
        ConsoleUtil.linha("[3] Movimentações de Estoque (Entradas/Saídas)");
        ConsoleUtil.linha("[4] Relatórios");
        ConsoleUtil.linha("[5] Dashboard Resumo");
        ConsoleUtil.linha("[0] Voltar ao Menu Principal");
        System.out.println();
    }

    // ==================== PRODUTOS ====================
    private void menuProdutos() {
        boolean ativo = true;
        while (ativo) {
            ConsoleUtil.titulo("Gerenciar Produtos");
            ConsoleUtil.linha("[1] Listar todos os produtos");
            ConsoleUtil.linha("[2] Buscar produto por nome");
            ConsoleUtil.linha("[3] Cadastrar novo produto");
            ConsoleUtil.linha("[4] Editar produto");
            ConsoleUtil.linha("[5] Excluir produto (desativar)");
            ConsoleUtil.linha("[0] Voltar");
            System.out.println();

            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, 5);
            switch (opcao) {
                case 1 -> listarProdutos();
                case 2 -> buscarProdutoPorNome();
                case 3 -> cadastrarProduto();
                case 4 -> editarProduto();
                case 5 -> excluirProduto();
                case 0 -> ativo = false;
            }
            if (ativo)
                ConsoleUtil.pausar();
        }
    }

    private void listarProdutos() {
        ConsoleUtil.subtitulo("Lista de Produtos");
        List<Produto> produtos = produtoDAO.listarTodos();
        if (produtos.isEmpty()) {
            ConsoleUtil.aviso("Nenhum produto cadastrado.");
            return;
        }
        // for com continue: pula produtos sem preço de venda definido na exibição de
        // alerta
        for (Produto p : produtos) {
            System.out.println("  " + p);
            if (p.estoqueAbaixoMinimo()) {
                ConsoleUtil.aviso("  Produto com estoque abaixo do mínimo!");
            }
        }
        System.out.println("\n  Total: " + produtos.size() + " produto(s).");
    }

    private void buscarProdutoPorNome() {
        String nome = ConsoleUtil.lerString("  Nome do produto (parcial): ");
        List<Produto> resultado = produtoDAO.buscarPorNome(nome);
        if (resultado.isEmpty()) {
            ConsoleUtil.aviso("Nenhum produto encontrado para: " + nome);
            return;
        }
        resultado.forEach(p -> System.out.println("  " + p));
    }

    private void cadastrarProduto() {
        ConsoleUtil.subtitulo("Cadastrar Novo Produto");
        Produto p = new Produto();

        try {
            p.setNome(ConsoleUtil.lerString("  Nome: "));
            p.setDescricao(ConsoleUtil.lerStringOpcional("  Descrição (opcional): "));
            p.setCategoria(ConsoleUtil.lerStringOpcional("  Categoria (opcional): "));
            p.setPrecoCompra(ConsoleUtil.lerDecimal("  Preço de compra (R$): "));
            p.setPrecoVenda(ConsoleUtil.lerDecimal("  Preço de venda  (R$): "));
            p.setQuantidadeEstoque(ConsoleUtil.lerInt("  Quantidade inicial em estoque: "));
            p.setEstoqueMinimo(ConsoleUtil.lerInt("  Estoque mínimo (padrão 10): "));

            // Selecionar fornecedor
            listarFornecedoresResumido();
            int fId = ConsoleUtil.lerInt("  ID do fornecedor (0 = sem fornecedor): ");
            if (fId > 0)
                p.setFornecedorId(fId);

            int novoId = produtoDAO.inserir(p);
            if (novoId > 0) {
                ConsoleUtil.sucesso("Produto cadastrado com ID " + novoId + ".");
            } else {
                ConsoleUtil.erro("Falha ao cadastrar produto.");
            }

        } catch (IllegalArgumentException e) {
            ConsoleUtil.erro("Dado inválido: " + e.getMessage());
        }
    }

    private void editarProduto() {
        ConsoleUtil.subtitulo("Editar Produto");
        int id = ConsoleUtil.lerInt("  ID do produto: ");

        try {
            Produto p = produtoDAO.buscarPorId(id);
            if (p == null) {
                ConsoleUtil.aviso("Produto ID " + id + " não encontrado.");
                return;
            }

            System.out.println("  Produto atual: " + p);
            System.out.println("  (ENTER = manter valor atual)");

            String nome = ConsoleUtil.lerStringOpcional("  Novo nome [" + p.getNome() + "]: ");
            if (!nome.isBlank())
                p.setNome(nome);

            String desc = ConsoleUtil.lerStringOpcional("  Nova descrição [" + p.getDescricao() + "]: ");
            if (!desc.isBlank())
                p.setDescricao(desc);

            String cat = ConsoleUtil.lerStringOpcional("  Nova categoria [" + p.getCategoria() + "]: ");
            if (!cat.isBlank())
                p.setCategoria(cat);

            String pc = ConsoleUtil.lerStringOpcional("  Novo preço compra [" + p.getPrecoCompra() + "]: ");
            if (!pc.isBlank())
                p.setPrecoCompra(new BigDecimal(pc.replace(",", ".")));

            String pv = ConsoleUtil.lerStringOpcional("  Novo preço venda  [" + p.getPrecoVenda() + "]: ");
            if (!pv.isBlank())
                p.setPrecoVenda(new BigDecimal(pv.replace(",", ".")));

            String em = ConsoleUtil.lerStringOpcional("  Novo estoque mínimo [" + p.getEstoqueMinimo() + "]: ");
            if (!em.isBlank())
                p.setEstoqueMinimo(Integer.parseInt(em));

            if (produtoDAO.atualizar(p)) {
                ConsoleUtil.sucesso("Produto atualizado com sucesso.");
            } else {
                ConsoleUtil.erro("Nenhuma alteração salva.");
            }

        } catch (NumberFormatException e) {
            ConsoleUtil.erro("Valor numérico inválido: " + e.getMessage());
        }
    }

    private void excluirProduto() {
        int id = ConsoleUtil.lerInt("  ID do produto a excluir: ");
        if (!ConsoleUtil.confirmar("  Confirmar desativação do produto ID " + id + "?"))
            return;
        if (produtoDAO.excluir(id)) {
            ConsoleUtil.sucesso("Produto desativado com sucesso.");
        } else {
            ConsoleUtil.erro("Produto não encontrado.");
        }
    }

    // ==================== FORNECEDORES ====================
    private void menuFornecedores() {
        boolean ativo = true;
        while (ativo) {
            ConsoleUtil.titulo("Gerenciar Fornecedores");
            ConsoleUtil.linha("[1] Listar todos os fornecedores");
            ConsoleUtil.linha("[2] Cadastrar fornecedor");
            ConsoleUtil.linha("[3] Editar fornecedor");
            ConsoleUtil.linha("[4] Excluir fornecedor");
            ConsoleUtil.linha("[0] Voltar");
            System.out.println();

            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, 4);
            switch (opcao) {
                case 1 -> listarFornecedores();
                case 2 -> cadastrarFornecedor();
                case 3 -> editarFornecedor();
                case 4 -> excluirFornecedor();
                case 0 -> ativo = false;
            }
            if (ativo)
                ConsoleUtil.pausar();
        }
    }

    private void listarFornecedores() {
        ConsoleUtil.subtitulo("Lista de Fornecedores");
        List<Fornecedor> lista = fornecedorDAO.listarTodos();
        if (lista.isEmpty()) {
            ConsoleUtil.aviso("Nenhum fornecedor cadastrado.");
            return;
        }
        lista.forEach(f -> System.out.println("  " + f));
    }

    private void listarFornecedoresResumido() {
        List<Fornecedor> lista = fornecedorDAO.listarTodos();
        if (lista.isEmpty()) {
            ConsoleUtil.aviso("  Nenhum fornecedor cadastrado.");
            return;
        }
        System.out.println("  --- Fornecedores disponíveis ---");
        // for com index explícito
        for (int i = 0; i < lista.size(); i++) {
            Fornecedor f = lista.get(i);
            System.out.printf("  ID: %-4d | %s%n", f.getId(), f.getNome());
        }
    }

    /**
     * Cadastra fornecedor com busca de endereço via API ViaCEP.
     * Demonstra integração REST, tratamento de exceções de rede e fallback manual.
     */
    private void cadastrarFornecedor() {
        ConsoleUtil.subtitulo("Cadastrar Fornecedor");
        Fornecedor f = new Fornecedor();

        f.setNome(ConsoleUtil.lerString("  Nome do fornecedor: "));
        f.setContato(ConsoleUtil.lerStringOpcional("  Telefone/contato: "));

        // ---- Integração ViaCEP (diferencial) ----
        Endereco endereco = buscarEnderecoViaCep();
        f.setEndereco(endereco);

        int novoId = fornecedorDAO.inserir(f);
        if (novoId > 0) {
            ConsoleUtil.sucesso("Fornecedor cadastrado com ID " + novoId + ".");
        } else {
            ConsoleUtil.erro("Falha ao cadastrar fornecedor.");
        }
    }

    /**
     * Busca endereço via ViaCEP com fallback para entrada manual.
     * Trata: IOException (rede), IllegalArgumentException (CEP inválido), CEP não
     * encontrado.
     */
    private Endereco buscarEnderecoViaCep() {
        Endereco endereco = new Endereco();

        String cep = ConsoleUtil.lerStringOpcional("  CEP (somente números, ENTER para pular): ");

        if (!cep.isBlank()) {
            // try/catch/finally (requisito obrigatório)
            try {
                ConsoleUtil.info("Buscando CEP " + cep + "...");
                Endereco encontrado = viaCepClient.buscarEndereco(cep);

                if (encontrado == null) {
                    // CEP retornou {"erro": true}
                    ConsoleUtil.aviso("CEP não encontrado. Preencha manualmente.");
                    preencherEnderecoManual(endereco);
                } else {
                    // Exibe endereço encontrado e permite confirmar/corrigir
                    System.out.println();
                    ConsoleUtil.sucesso("Endereço encontrado:");
                    System.out.println("  Logradouro : " + encontrado.getLogradouro());
                    System.out.println("  Bairro     : " + encontrado.getBairro());
                    System.out.println("  Cidade/UF  : " + encontrado.getCidade() + " / " + encontrado.getUf());
                    System.out.println("  CEP        : " + encontrado.getCep());
                    System.out.println();

                    if (ConsoleUtil.confirmar("  Confirmar dados do endereço?")) {
                        endereco = encontrado;
                        // Número e complemento precisam ser digitados
                        endereco.setNumero(ConsoleUtil.lerStringOpcional("  Número: "));
                        endereco.setComplemento(ConsoleUtil.lerStringOpcional("  Complemento (opcional): "));
                    } else {
                        ConsoleUtil.info("Preencha os campos manualmente:");
                        endereco.setCep(encontrado.getCep());
                        preencherEnderecoManual(endereco);
                    }
                }

            } catch (IllegalArgumentException e) {
                ConsoleUtil.aviso("CEP inválido: " + e.getMessage());
                preencherEnderecoManual(endereco);

            } catch (IOException e) {
                ConsoleUtil.aviso("Falha de rede ao acessar: " + e.getMessage());
                ConsoleUtil.info("Preenchimento manual ativado como fallback.");
                preencherEnderecoManual(endereco);

            } finally {
                // finally sempre executado - log de auditoria da tentativa
                System.out.println("  Consulta finalizada.");
            }
        } else {
            // Usuário pulou a busca por CEP - entrada manual
            ConsoleUtil.info("CEP não informado. Preencha o endereço manualmente:");
            preencherEnderecoManual(endereco);
        }

        return endereco;
    }

    /** Preenchimento manual de todos os campos de endereço. */
    private void preencherEnderecoManual(Endereco end) {
        String cepManual = ConsoleUtil.lerStringOpcional("  CEP: ");
        if (!cepManual.isBlank())
            end.setCep(cepManual);

        String log = ConsoleUtil.lerStringOpcional("  Logradouro: ");
        if (!log.isBlank())
            end.setLogradouro(log);

        end.setNumero(ConsoleUtil.lerStringOpcional("  Número: "));
        end.setComplemento(ConsoleUtil.lerStringOpcional("  Complemento: "));

        String bairro = ConsoleUtil.lerStringOpcional("  Bairro: ");
        if (!bairro.isBlank())
            end.setBairro(bairro);

        String cidade = ConsoleUtil.lerStringOpcional("  Cidade: ");
        if (!cidade.isBlank())
            end.setCidade(cidade);

        String uf = ConsoleUtil.lerStringOpcional("  UF (ex: SP): ");
        if (!uf.isBlank())
            end.setUf(uf.toUpperCase());
    }

    private void editarFornecedor() {
        ConsoleUtil.subtitulo("Editar Fornecedor");
        int id = ConsoleUtil.lerInt("  ID do fornecedor: ");
        Fornecedor f = fornecedorDAO.buscarPorId(id);
        if (f == null) {
            ConsoleUtil.aviso("Fornecedor ID " + id + " não encontrado.");
            return;
        }
        System.out.println("  Fornecedor atual: " + f);

        String nome = ConsoleUtil.lerStringOpcional("  Novo nome [" + f.getNome() + "]: ");
        if (!nome.isBlank())
            f.setNome(nome);

        String contato = ConsoleUtil.lerStringOpcional("  Novo contato [" + f.getContato() + "]: ");
        if (!contato.isBlank())
            f.setContato(contato);

        if (ConsoleUtil.confirmar("  Atualizar endereço?")) {
            Endereco novoEnd = buscarEnderecoViaCep();
            f.setEndereco(novoEnd);
        }

        if (fornecedorDAO.atualizar(f)) {
            ConsoleUtil.sucesso("Fornecedor atualizado.");
        } else {
            ConsoleUtil.erro("Falha ao atualizar.");
        }
    }

    private void excluirFornecedor() {
        int id = ConsoleUtil.lerInt("  ID do fornecedor a excluir: ");
        if (!ConsoleUtil.confirmar("  Confirmar exclusão do fornecedor ID " + id + "?"))
            return;
        try {
            if (fornecedorDAO.excluir(id)) {
                ConsoleUtil.sucesso("Fornecedor excluído.");
            } else {
                ConsoleUtil.erro("Fornecedor não encontrado.");
            }
        } catch (IllegalStateException e) {
            ConsoleUtil.erro(e.getMessage());
        }
    }

    // ==================== MOVIMENTAÇÕES ====================
    private void menuMovimentacoes() {
        boolean ativo = true;
        while (ativo) {
            ConsoleUtil.titulo("Movimentações de Estoque");
            ConsoleUtil.linha("[1] Registrar ENTRADA (compra)");
            ConsoleUtil.linha("[2] Registrar SAÍDA   (venda)");
            ConsoleUtil.linha("[3] Listar movimentações de um produto");
            ConsoleUtil.linha("[4] Listar todas as movimentações");
            ConsoleUtil.linha("[0] Voltar");
            System.out.println();

            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, 4);
            switch (opcao) {
                case 1 -> registrarMovimentacao(MovimentacaoEstoque.Tipo.ENTRADA);
                case 2 -> registrarMovimentacao(MovimentacaoEstoque.Tipo.SAIDA);
                case 3 -> listarMovimentacoesProduto();
                case 4 -> listarTodasMovimentacoes();
                case 0 -> ativo = false;
            }
            if (ativo)
                ConsoleUtil.pausar();
        }
    }

    private void registrarMovimentacao(MovimentacaoEstoque.Tipo tipo) {
        String tipoStr = tipo == MovimentacaoEstoque.Tipo.ENTRADA ? "ENTRADA" : "SAÍDA";
        ConsoleUtil.subtitulo("Registrar " + tipoStr);

        // Exibe lista de produtos para facilitar escolha
        listarProdutos();

        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setProdutoId(ConsoleUtil.lerInt("  ID do produto: "));
        mov.setTipo(tipo);
        mov.setQuantidade(ConsoleUtil.lerInt("  Quantidade: "));

        // Busca preço sugerido
        Produto produtoHint = produtoDAO.buscarPorId(mov.getProdutoId());
        if (produtoHint != null) {
            BigDecimal precoSugerido = tipo == MovimentacaoEstoque.Tipo.ENTRADA
                    ? produtoHint.getPrecoCompra()
                    : produtoHint.getPrecoVenda();
            ConsoleUtil.info("Preço sugerido: R$ " + precoSugerido);
        }

        mov.setValorUnitario(ConsoleUtil.lerDecimal("  Valor unitário (R$): "));
        mov.setObservacao(ConsoleUtil.lerStringOpcional("  Observação (opcional): "));

        try {
            int novoId = movimentacaoDAO.registrar(mov);
            if (novoId > 0) {
                ConsoleUtil.sucesso(tipoStr + " registrada com ID " + novoId + ".");
                ConsoleUtil.info("Total: R$ " + mov.getValorTotal());
            } else {
                ConsoleUtil.erro("Falha ao registrar movimentação.");
            }
        } catch (IllegalStateException e) {
            ConsoleUtil.erro(e.getMessage());
        }
    }

    private void listarMovimentacoesProduto() {
        int prodId = ConsoleUtil.lerInt("  ID do produto: ");
        List<MovimentacaoEstoque> lista = movimentacaoDAO.listarPorProduto(prodId);
        if (lista.isEmpty()) {
            ConsoleUtil.aviso("Nenhuma movimentação encontrada para o produto ID " + prodId);
            return;
        }
        lista.forEach(m -> System.out.println("  " + m));
    }

    private void listarTodasMovimentacoes() {
        ConsoleUtil.subtitulo("Todas as Movimentações");
        List<MovimentacaoEstoque> lista = movimentacaoDAO.listarTodas();
        if (lista.isEmpty()) {
            ConsoleUtil.aviso("Nenhuma movimentação registrada.");
            return;
        }
        // Uso de for com break: exibe no máximo 50 registros para não sobrecarregar
        // terminal
        int count = 0;
        for (MovimentacaoEstoque m : lista) {
            System.out.println("  " + m);
            count++;
            if (count >= 50) {
                ConsoleUtil.aviso("Exibindo apenas os 50 mais recentes. Total: " + lista.size());
                break; // break explícito (requisito obrigatório)
            }
        }
    }

    // ==================== RELATÓRIOS ====================
    private void menuRelatorios() {
        boolean ativo = true;
        while (ativo) {
            ConsoleUtil.titulo("Relatórios");

            // Array de relatórios - demonstra uso de for com índice
            Relatorio[] relatorios = { relEstoque, relGastos, relPrevisao };

            for (int i = 0; i < relatorios.length; i++) {
                System.out.printf("  [%d] %s%n", i + 1, relatorios[i].getTitulo());
            }
            ConsoleUtil.linha("[0] Voltar");
            System.out.println();

            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, relatorios.length);

            if (opcao == 0) {
                ativo = false;
                continue; // continue explícito (requisito obrigatório)
            }

            relatorios[opcao - 1].gerar();
            ConsoleUtil.pausar();
        }
    }

    // ==================== DASHBOARD ====================
    private void exibirDashboard() {
        ConsoleUtil.titulo("Dashboard - Resumo Geral");

        List<Produto> produtos = produtoDAO.listarTodos();
        BigDecimal totalVendasMes = movimentacaoDAO.totalVendasMesAtual();
        BigDecimal totalGastosMes = movimentacaoDAO.totalGastosMesAtual();
        BigDecimal lucroLiquidoMes = totalVendasMes.subtract(totalGastosMes);

        // Totais de estoque
        int totalItens = 0;
        BigDecimal valorCusto = BigDecimal.ZERO;
        BigDecimal valorVenda = BigDecimal.ZERO;
        int abaixoMinimo = 0;

        for (Produto p : produtos) {
            totalItens += p.getQuantidadeEstoque();
            valorCusto = valorCusto.add(
                    p.getPrecoCompra().multiply(BigDecimal.valueOf(p.getQuantidadeEstoque())));
            valorVenda = valorVenda.add(
                    p.getPrecoVenda().multiply(BigDecimal.valueOf(p.getQuantidadeEstoque())));
            if (p.estoqueAbaixoMinimo())
                abaixoMinimo++;
        }

        System.out.println();
        ConsoleUtil.linha("═══════════════════════════════════════════════════");
        System.out.printf("  %-35s %d%n", "Total de produtos cadastrados:", produtos.size());
        System.out.printf("  %-35s %d%n", "Total de itens em estoque:", totalItens);
        System.out.printf("  %-35s R$ %,.2f%n", "Valor de custo em estoque:", valorCusto);
        System.out.printf("  %-35s R$ %,.2f%n", "Valor de venda em estoque:", valorVenda);
        System.out.printf("  %-35s %d%n", "Produtos abaixo do mínimo:", abaixoMinimo);
        ConsoleUtil.linha("───────────────────────────────────────────────────");
        System.out.printf("  %-35s R$ %,.2f%n", "Vendas no mês atual:", totalVendasMes);
        System.out.printf("  %-35s R$ %,.2f%n", "Gastos no mês atual:", totalGastosMes);
        System.out.printf("  %-35s R$ %,.2f%n", "Lucro líquido do mês:", lucroLiquidoMes);
        ConsoleUtil.linha("═══════════════════════════════════════════════════");

        // if/else if/else (requisito obrigatório)
        if (lucroLiquidoMes.compareTo(BigDecimal.ZERO) > 0) {
            ConsoleUtil.sucesso("Mês rentável! Lucro positivo.");
        } else if (lucroLiquidoMes.compareTo(BigDecimal.ZERO) == 0) {
            ConsoleUtil.aviso("Mês em equilíbrio (lucro zero).");
        } else {
            ConsoleUtil.erro("Mês no prejuízo! Revise os custos.");
        }

        if (abaixoMinimo > 0) {
            ConsoleUtil.aviso(abaixoMinimo + " produto(s) com estoque abaixo do mínimo!");
        }
    }
}
