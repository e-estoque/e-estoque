package com.estoque;

import com.estoque.view.MenuPrincipal;

/**
 * Ponto de entrada do Sistema de Controle de Estoque.
 * Executa exclusivamente no terminal (linha de comando).
 */
public class Main {
    public static void main(String[] args) {
        MenuPrincipal menu = new MenuPrincipal();
        menu.iniciar();
    }
}
