import java.util.Scanner;

public class exercicio03 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double cotacao = 4.95;
        double valor_dolar;

        System.out.print("Digite o valor em Dólar: ");
        valor_dolar = scanner.nextDouble();

        double conversao = valor_dolar * cotacao;

        System.out.printf("Valor convertido: R$ %.2f", conversao);
        scanner.close();
    }
}