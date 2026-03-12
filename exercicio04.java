import java.util.Scanner;

public class exercicio04 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double salarioAtual;
        System.out.print("Digite o salário atual: ");
        salarioAtual = scanner.nextDouble();

        System.out.println("Salário atual: R$ " + salarioAtual);
        double salarioNovo;
        salarioNovo = salarioAtual * 1.07;
        System.out.printf("Novo salário: R$ %.2f", salarioNovo);

        scanner.close();
    }
}