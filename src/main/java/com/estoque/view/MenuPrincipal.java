package com.estoque.view;

import com.estoque.util.ConsoleUtil;

public class MenuPrincipal {

    private final MenuLojista menuLojista = new MenuLojista();
    private final MenuCliente menuCliente = new MenuCliente();

    public void iniciar() {
        exibirBannerInicial();

        boolean executando = true;
        while (executando) {
            exibirMenuPrincipal();
            int opcao = ConsoleUtil.lerIntIntervalo("  Opção: ", 0, 2);

            switch (opcao) {
                case 1 -> menuLojista.iniciar();
                case 2 -> menuCliente.iniciar();
                case 0 -> {
                    ConsoleUtil.titulo("Até logo!");
                    System.out.println("  Obrigado por usar o sistema Minas Fogões!");
                    System.out.println();
                    executando = false;
                }
                default -> ConsoleUtil.aviso("Opção inválida.");
            }
        }
    }

    private void exibirMenuPrincipal() {
        ConsoleUtil.titulo("Minas Fogões - Sistema de Estoque");
        ConsoleUtil.linha("Selecione o perfil de acesso:");
        System.out.println();
        ConsoleUtil.linha("[1] Lojista  (acesso administrativo)");
        ConsoleUtil.linha("[2] Cliente  (consulta de produtos)");
        ConsoleUtil.linha("[0] Sair");
        System.out.println();
    }

    private void exibirBannerInicial() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║     SISTEMA DE CONTROLE DE ESTOQUE               ║");
        System.out.println("  ║     INTEGRADO AO ATENDIMENTO MINAS FOGÕES        ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println();
    }
}
