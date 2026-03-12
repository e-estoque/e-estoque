import java.util.Scanner;

public class exercicio01 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Double numero1;
        Double numero2;

        System.out.println("Digite o primeiro número:");
        numero1 = scanner.nextDouble();

        System.out.println("Digite o segundo número:");
        numero2 = scanner.nextDouble();

        System.out.println("Adição: " + (numero1 + numero2));

        System.out.println("Subtração: " + (numero1 - numero2));
        System.out.println("Multiplicação: " + (numero1 * numero2));
        System.out.println("Divisão: " + (numero1 / numero2));

        scanner.close();
    }
}