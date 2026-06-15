package com.estoque.dao;

import com.estoque.model.Fornecedor;
import com.estoque.model.Produto;

import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    public int inserir(Fornecedor f) {
        int id = Dados.nextFornecedorId();
        f.setId(id);
        Dados.fornecedores.put(id, f);
        return id;
    }

    public List<Fornecedor> listarTodos() {
        List<Fornecedor> lista = new ArrayList<>(Dados.fornecedores.values());
        lista.sort((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()));
        return lista;
    }

    public Fornecedor buscarPorId(int id) {
        return Dados.fornecedores.get(id);
    }

    public boolean atualizar(Fornecedor f) {
        if (!Dados.fornecedores.containsKey(f.getId())) {
            return false;
        }
        Dados.fornecedores.put(f.getId(), f);
        return true;
    }

    public boolean excluir(int id) {
        for (Produto p : Dados.produtos.values()) {
            if (p.isAtivo() && p.getFornecedorId() == id) {
                throw new IllegalStateException(
                    "Não é possível excluir: fornecedor possui produtos ativos vinculados.");
            }
        }
        return Dados.fornecedores.remove(id) != null;
    }
}
