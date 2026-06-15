package com.estoque.dao;

import com.estoque.model.Fornecedor;
import com.estoque.model.Produto;

import java.util.ArrayList;
import java.util.List;

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
        List<Produto> lista = new ArrayList<>();
        for (Produto p : Dados.produtos.values()) {
            if (p.isAtivo()) {
                resolverNomeFornecedor(p);
                lista.add(p);
            }
        }
        lista.sort((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()));
        return lista;
    }

    public Produto buscarPorId(int id) {
        Produto p = Dados.produtos.get(id);
        if (p == null || !p.isAtivo()) {
            return null;
        }
        resolverNomeFornecedor(p);
        return p;
    }

    public List<Produto> buscarPorNome(String nome) {
        String nomeLower = nome.toLowerCase();
        List<Produto> lista = new ArrayList<>();
        for (Produto p : Dados.produtos.values()) {
            if (p.isAtivo() && p.getNome().toLowerCase().contains(nomeLower)) {
                resolverNomeFornecedor(p);
                lista.add(p);
            }
        }
        lista.sort((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()));
        return lista;
    }

    public List<Produto> listarAbaixoEstoqueMinimo() {
        List<Produto> lista = new ArrayList<>();
        for (Produto p : Dados.produtos.values()) {
            if (p.isAtivo() && p.getQuantidadeEstoque() < p.getEstoqueMinimo()) {
                resolverNomeFornecedor(p);
                lista.add(p);
            }
        }
        lista.sort((a, b) -> Integer.compare(a.getQuantidadeEstoque(), b.getQuantidadeEstoque()));
        return lista;
    }

    public boolean atualizar(Produto produto) {
        if (!Dados.produtos.containsKey(produto.getId())) {
            return false;
        }
        resolverNomeFornecedor(produto);
        Dados.produtos.put(produto.getId(), produto);
        return true;
    }

    public boolean excluir(int id) {
        Produto p = Dados.produtos.get(id);
        if (p == null) {
            return false;
        }
        p.setAtivo(false);
        return true;
    }

    public boolean atualizarQuantidade(int produtoId, int novaQuantidade) {
        Produto p = Dados.produtos.get(produtoId);
        if (p == null) {
            return false;
        }
        p.setQuantidadeEstoque(novaQuantidade);
        return true;
    }

    private void resolverNomeFornecedor(Produto p) {
        if (p.getFornecedorId() > 0) {
            Fornecedor f = Dados.fornecedores.get(p.getFornecedorId());
            if (f != null) {
                p.setNomeFornecedor(f.getNome());
            }
        }
    }
}
