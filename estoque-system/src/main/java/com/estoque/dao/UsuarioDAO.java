package com.estoque.dao;

import com.estoque.model.Usuario;

public class UsuarioDAO {

    public Usuario autenticar(String login, String senha) {
        return Dados.usuarios.stream()
                .filter(u -> u.getLogin().equals(login) && u.getSenha().equals(senha))
                .findFirst()
                .orElse(null);
    }
}
