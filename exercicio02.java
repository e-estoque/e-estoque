import java.util.Scanner;

public class exercicio02 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int X;
        int Y;
        System.out.println("Digite o X:");
        X = scanner.nextInt();

        System.out.println("Digite o Y:");
        Y = scanner.nextInt();
        System.out.printf("O resto da divisao de %d por %d é: %d", X, Y, X % Y);

        scanner.close();

    }

}
