import java.util.Scanner;

public class exercicio05 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double nota1;
        double nota2;
        double nota3;
        System.out.println("Digite a nota 1:");
        nota1 = scanner.nextDouble();
        System.out.println("Digite a nota 2: ");
        nota2 = scanner.nextDouble();
        System.out.println("DIgite a nota 3: ");
        nota3 = scanner.nextDouble();
        double media;
        media = (nota1 + nota2 + nota3) / 3;
        System.out.printf("Média: %.2f", media);

        scanner.close();

    }
}
