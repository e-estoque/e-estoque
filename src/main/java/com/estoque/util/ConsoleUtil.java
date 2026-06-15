package com.estoque.util;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleUtil {

    private static final Scanner scanner = new Scanner(System.in);
    private static final int LARGURA = 70;

    public static String lerString(String prompt) {
        String entrada;
        do {
            System.out.print(prompt);
            entrada = scanner.nextLine().trim();
            if (entrada.isBlank()) {
                System.out.println("  [!]  Entrada não pode ser vazia. Tente novamente.");
            }
        } while (entrada.isBlank());
        return entrada;
    }

    public static String lerStringOpcional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

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

    public static boolean confirmar(String prompt) {
        while (true) {
            System.out.print(prompt + " (S/N): ");
            String resp = scanner.nextLine().trim().toUpperCase();
            if (resp.equals("S")) return true;
            if (resp.equals("N")) return false;
            System.out.println("  [!] Digite S para sim ou N para não.");
        }
    }

    public static void pausar() {
        System.out.print("\n  Pressione ENTER para continuar...");
        scanner.nextLine();
    }

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
