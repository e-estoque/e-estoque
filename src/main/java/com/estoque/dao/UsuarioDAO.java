package com.estoque.dao;

import com.estoque.model.Usuario;

public class UsuarioDAO {

    public Usuario autenticar(String login, String senha) {
        for (Usuario u : Dados.usuarios) {
            if (u.getLogin().equals(login) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }
}
