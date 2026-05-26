package com.estoque.util;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Utilitários para leitura de entrada do usuário e formatação de terminal.
 * Centraliza o Scanner e helpers de I/O para evitar repetição.
 */
public class ConsoleUtil {

    // Scanner único para toda a aplicação
    private static final Scanner scanner = new Scanner(System.in);

    // Largura padrão do separador
    private static final int LARGURA = 70;

    // ------------------------------------------------------------------
    // Leitura de dados
    // ------------------------------------------------------------------

    /** Lê uma linha de texto (não vazia) do terminal. */
    public static String lerString(String prompt) {
        String entrada;
        do {
            System.out.print(prompt);
            entrada = scanner.nextLine().trim();
            if (entrada.isBlank()) {
                System.out.println("  [!]  Entrada não pode ser vazia. Tente novamente.");
            }
        } while (entrada.isBlank()); // do-while garante ao menos uma leitura
        return entrada;
    }

    /** Lê uma linha de texto que pode ser vazia. */
    public static String lerStringOpcional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Lê um inteiro com validação. Repete até receber valor válido. */
    public static int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Valor inválido. Digite um número inteiro.");
            }
        }
    }

    /** Lê um inteiro dentro de um intervalo [min, max]. */
    public static int lerIntIntervalo(String prompt, int min, int max) {
        int valor;
        do {
            valor = lerInt(prompt);
            if (valor < min || valor > max) {
                System.out.printf("  [!] Valor fora do intervalo [%d – %d].%n", min, max);
            }
        } while (valor < min || valor > max);
        return valor;
    }

    /** Lê um BigDecimal positivo. */
    public static BigDecimal lerDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                BigDecimal valor = new BigDecimal(entrada);
                if (valor.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("  [!] O valor não pode ser negativo.");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("  [!] Valor inválido. Use ponto ou vírgula como separador decimal.");
            }
        }
    }

    /** Lê confirmação S/N. Retorna true para 'S'. */
    public static boolean confirmar(String prompt) {
        while (true) {
            System.out.print(prompt + " (S/N): ");
            String resp = scanner.nextLine().trim().toUpperCase();
            if (resp.equals("S")) return true;
            if (resp.equals("N")) return false;
            System.out.println("  [!] Digite S para sim ou N para não.");
        }
    }

    /** Pausa e aguarda ENTER do usuário. */
    public static void pausar() {
        System.out.print("\n  Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    // ------------------------------------------------------------------
    // Formatação de terminal
    // ------------------------------------------------------------------

    public static void separador() {
        System.out.println("─".repeat(LARGURA));
    }

    public static void separadorDuplo() {
        System.out.println("═".repeat(LARGURA));
    }

    public static void titulo(String texto) {
        System.out.println();
        separadorDuplo();
        System.out.printf("  %s%n", texto.toUpperCase());
        separadorDuplo();
    }

    public static void subtitulo(String texto) {
        System.out.println();
        separador();
        System.out.printf("  %s%n", texto);
        separador();
    }

    public static void sucesso(String msg) {
        System.out.println("\n  [OK] " + msg);
    }

    public static void erro(String msg) {
        System.out.println("\n  [ERRO] " + msg);
    }

    public static void aviso(String msg) {
        System.out.println("\n  [!] " + msg);
    }

    public static void info(String msg) {
        System.out.println("  [i] " + msg);
    }

    public static void linha(String msg) {
        System.out.println("  " + msg);
    }
}
