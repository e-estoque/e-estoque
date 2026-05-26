package com.estoque.dao;

import com.estoque.model.Fornecedor;
import com.estoque.model.Produto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoDAO {

    public int inserir(Produto produto) {
        int id = Dados.nextProdutoId();
        produto.setId(id);
        produto.setAtivo(true);
        resolverNomeFornecedor(produto);
        Dados.produtos.put(id, produto);
        return id;
    }

    public List<Produto> listarTodos() {
        return Dados.produtos.values().stream()
            .filter(Produto::isAtivo)
            .sorted(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER))
            .map(this::comNomeFornecedor)
            .collect(Collectors.toList());
    }

    public Produto buscarPorId(int id) {
        Produto p = Dados.produtos.get(id);
        if (p == null || !p.isAtivo()) return null;
        return comNomeFornecedor(p);
    }

    public List<Produto> buscarPorNome(String nome) {
        String lower = nome.toLowerCase();
        return Dados.produtos.values().stream()
            .filter(p -> p.isAtivo() && p.getNome().toLowerCase().contains(lower))
            .sorted(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER))
            .map(this::comNomeFornecedor)
            .collect(Collectors.toList());
    }

    public List<Produto> listarAbaixoEstoqueMinimo() {
        return Dados.produtos.values().stream()
            .filter(p -> p.isAtivo() && p.getQuantidadeEstoque() < p.getEstoqueMinimo())
            .sorted(Comparator.comparingInt(Produto::getQuantidadeEstoque))
            .map(this::comNomeFornecedor)
            .collect(Collectors.toList());
    }

    public boolean atualizar(Produto produto) {
        if (!Dados.produtos.containsKey(produto.getId())) return false;
        resolverNomeFornecedor(produto);
        Dados.produtos.put(produto.getId(), produto);
        return true;
    }

    public boolean excluir(int id) {
        Produto p = Dados.produtos.get(id);
        if (p == null) return false;
        p.setAtivo(false);
        return true;
    }

    public boolean atualizarQuantidade(int produtoId, int novaQuantidade) {
        Produto p = Dados.produtos.get(produtoId);
        if (p == null) return false;
        p.setQuantidadeEstoque(novaQuantidade);
        return true;
    }

    // ------------------------------------------------------------------
    private Produto comNomeFornecedor(Produto p) {
        resolverNomeFornecedor(p);
        return p;
    }

    private void resolverNomeFornecedor(Produto p) {
        if (p.getFornecedorId() > 0) {
            Fornecedor f = Dados.fornecedores.get(p.getFornecedorId());
            if (f != null) p.setNomeFornecedor(f.getNome());
        }
    }
}
