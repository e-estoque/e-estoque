package com.estoque.dao;

import com.estoque.model.Fornecedor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FornecedorDAO {

    public int inserir(Fornecedor f) {
        int id = Dados.nextFornecedorId();
        f.setId(id);
        Dados.fornecedores.put(id, f);
        return id;
    }

    public List<Fornecedor> listarTodos() {
        return Dados.fornecedores.values().stream()
            .sorted(Comparator.comparing(Fornecedor::getNome, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
    }

    public Fornecedor buscarPorId(int id) {
        return Dados.fornecedores.get(id);
    }

    public boolean atualizar(Fornecedor f) {
        if (!Dados.fornecedores.containsKey(f.getId())) return false;
        Dados.fornecedores.put(f.getId(), f);
        return true;
    }

    public boolean excluir(int id) {
        boolean temProdutosAtivos = Dados.produtos.values().stream()
            .anyMatch(p -> p.isAtivo() && p.getFornecedorId() == id);
        if (temProdutosAtivos) {
            throw new IllegalStateException(
                "Não é possível excluir: fornecedor possui produtos ativos vinculados.");
        }
        return Dados.fornecedores.remove(id) != null;
    }
}
