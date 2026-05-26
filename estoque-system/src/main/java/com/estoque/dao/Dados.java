package com.estoque.dao;

import com.estoque.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositório central em memória. Substitui o banco de dados MySQL.
 * Inicializado com dados de demonstração no bloco estático.
 */
public class Dados {

    public static final String LOJA_NOME = "Minas Fogões - Estoque e Atendimento";
    public static final String LOJA_WHATSAPP = "(11) 91234-5678";

    private static final AtomicInteger SEQ_PRODUTO = new AtomicInteger(0);
    private static final AtomicInteger SEQ_FORNECEDOR = new AtomicInteger(0);
    private static final AtomicInteger SEQ_MOVIMENTACAO = new AtomicInteger(0);

    public static final Map<Integer, Fornecedor> fornecedores = new LinkedHashMap<>();
    public static final Map<Integer, Produto> produtos = new LinkedHashMap<>();
    public static final List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();
    public static final List<Usuario> usuarios = new ArrayList<>();

    static {
        carregarDadosIniciais();
    }

    // ------------------------------------------------------------------
    // Geradores de ID
    // ------------------------------------------------------------------
    public static int nextProdutoId() {
        return SEQ_PRODUTO.incrementAndGet();
    }

    public static int nextFornecedorId() {
        return SEQ_FORNECEDOR.incrementAndGet();
    }

    public static int nextMovimentacaoId() {
        return SEQ_MOVIMENTACAO.incrementAndGet();
    }

    public static String getPropriedade(String chave) {
        return switch (chave) {
            case "loja.nome" -> LOJA_NOME;
            case "loja.whatsapp" -> LOJA_WHATSAPP;
            default -> "";
        };
    }

    // ------------------------------------------------------------------
    // Dados iniciais de demonstração
    // ------------------------------------------------------------------
    private static void carregarDadosIniciais() {
        // Usuário admin
        Usuario admin = new Usuario();
        admin.setId(1);
        admin.setLogin("admin");
        admin.setSenha("admin123");
        admin.setPerfil("LOJISTA");
        usuarios.add(admin);

        // Fornecedor
        Fornecedor f1 = criarFornecedor(
                "Distribuidora Gás e Fogões Ltda", "(11) 98765-4321",
                "09270-000", "Rua das Indústrias", "500", "", "Distrito Industrial", "Santo André", "SP");
        fornecedores.put(f1.getId(), f1);

        // Produtos
        Produto p1 = criarProduto("Fogão 4 Bocas", "Fogão de piso 4 bocas aço inox", "Fogões", "280.00", "499.90", 10,
                3, f1.getId());
        Produto p2 = criarProduto("Fogão 6 Bocas", "Fogão industrial 6 bocas", "Fogões", "520.00", "899.90", 5, 2,
                f1.getId());
        Produto p3 = criarProduto("Mangueira de Gás 1,20m", "Mangueira GLP c/ registro de segurança", "Mangueiras",
                "8.50", "18.90", 80, 20, f1.getId());
        Produto p4 = criarProduto("Mangueira de Gás 1,50m", "Mangueira GLP extra longa", "Mangueiras", "10.00", "22.90",
                60, 15, f1.getId());
        Produto p5 = criarProduto("Registro de Gás", "Registro esfera para GLP", "Válvulas", "9.00", "19.90", 45, 10,
                f1.getId());
        Produto p6 = criarProduto("Queimador Simples", "Queimador avulso para fogão", "Peças de Fogão", "4.50", "12.90",
                90, 20, f1.getId());
        Produto p7 = criarProduto("Queimador Duplo", "Queimador duplo chama tripla", "Peças de Fogão", "8.00", "21.90",
                50, 15, f1.getId());
        Produto p8 = criarProduto("Grelha de Fogão", "Grelha de ferro para fogão 4 bocas", "Peças de Fogão", "12.00",
                "27.90", 3, 10, f1.getId());
        Produto p9 = criarProduto("Churrasqueira de Mesa", "Churrasqueira elétrica portátil 1200W", "Churrasqueiras",
                "75.00", "149.90", 12, 4, f1.getId());
        Produto p10 = criarProduto("Churrasqueira Carvão P", "Churrasqueira de carvão 40x30cm", "Churrasqueiras",
                "35.00", "79.90", 8, 3, f1.getId());
        Produto p11 = criarProduto("Tubulação Cobre 1/2\"", "Tubo de cobre 1/2\" - metro", "Tubulações", "6.00",
                "13.50", 100, 25, f1.getId());
        Produto p12 = criarProduto("Tubulação Galvanizada", "Tubo galvanizado 3/4\" - metro", "Tubulações", "9.00",
                "19.00", 40, 10, f1.getId());

        for (Produto p : List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)) {
            p.setNomeFornecedor(f1.getNome());
            produtos.put(p.getId(), p);
        }

        // Movimentações de exemplo distribuídas em meses
        LocalDateTime h3 = LocalDateTime.now().minusMonths(3);
        LocalDateTime h2 = LocalDateTime.now().minusMonths(2);
        LocalDateTime h1 = LocalDateTime.now().minusMonths(1);
        LocalDateTime h0 = LocalDateTime.now();

        addMov(p1.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 10, "280.00", "Compra inicial fogões 4 bocas", h3);
        addMov(p2.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 5, "520.00", "Compra fogões industriais", h3);
        addMov(p3.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 80, "8.50", "Compra mangueiras 1,20m", h3);
        addMov(p6.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 90, "4.50", "Compra queimadores", h3);
        addMov(p11.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 100, "6.00", "Compra tubulação cobre", h3);
        addMov(p1.getId(), MovimentacaoEstoque.Tipo.SAIDA, 3, "499.90", "Venda fogões", h2);
        addMov(p3.getId(), MovimentacaoEstoque.Tipo.SAIDA, 20, "18.90", "Venda mangueiras", h2);
        addMov(p6.getId(), MovimentacaoEstoque.Tipo.SAIDA, 15, "12.90", "Venda queimadores", h2);
        addMov(p9.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 12, "75.00", "Compra churrasqueiras elétr.", h1);
        addMov(p10.getId(), MovimentacaoEstoque.Tipo.ENTRADA, 8, "35.00", "Compra churrasqueiras carvão", h1);
        addMov(p5.getId(), MovimentacaoEstoque.Tipo.SAIDA, 10, "19.90", "Venda registros", h1);
        addMov(p9.getId(), MovimentacaoEstoque.Tipo.SAIDA, 2, "149.90", "Venda churrasqueira elétrica", h0);
        addMov(p3.getId(), MovimentacaoEstoque.Tipo.SAIDA, 15, "18.90", "Venda mangueiras GLP", h0);
    }

    // ------------------------------------------------------------------
    // Helpers de criação
    // ------------------------------------------------------------------
    private static Fornecedor criarFornecedor(String nome, String contato,
            String cep, String logradouro, String numero, String complemento,
            String bairro, String cidade, String uf) {
        Endereco end = new Endereco();
        end.setCep(cep);
        end.setLogradouro(logradouro);
        end.setNumero(numero);
        end.setComplemento(complemento);
        end.setBairro(bairro);
        end.setCidade(cidade);
        end.setUf(uf);

        Fornecedor f = new Fornecedor();
        f.setId(nextFornecedorId());
        f.setNome(nome);
        f.setContato(contato);
        f.setEndereco(end);
        return f;
    }

    private static Produto criarProduto(String nome, String descricao, String categoria,
            String precoCompra, String precoVenda, int qtd, int estoqueMin, int fornecedorId) {
        Produto p = new Produto();
        p.setId(nextProdutoId());
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setCategoria(categoria);
        p.setPrecoCompra(new BigDecimal(precoCompra));
        p.setPrecoVenda(new BigDecimal(precoVenda));
        p.setQuantidadeEstoque(qtd);
        p.setEstoqueMinimo(estoqueMin);
        p.setFornecedorId(fornecedorId);
        p.setAtivo(true);
        return p;
    }

    private static void addMov(int produtoId, MovimentacaoEstoque.Tipo tipo,
            int qtd, String valorUnitario, String obs, LocalDateTime data) {
        MovimentacaoEstoque m = new MovimentacaoEstoque();
        m.setId(nextMovimentacaoId());
        m.setProdutoId(produtoId);
        m.setTipo(tipo);
        m.setQuantidade(qtd);
        m.setValorUnitario(new BigDecimal(valorUnitario));
        m.setObservacao(obs);
        m.setDataMovimentacao(data);
        movimentacoes.add(m);
    }
}
