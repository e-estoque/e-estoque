import java.util.Scanner;

public class exercicio09 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double valorProduto;
        double valorPago;
        System.out.println("DIgite o valor do Produto:");
        valorProduto = scanner.nextDouble();
        System.out.println("DIgite o valor pago:");
        valorPago = scanner.nextDouble();
        double troco;
        troco = valorPago - valorProduto;

        if (valorPago > valorProduto) {
            System.out.printf("Seu troco é de: %.2f%n", troco);
        } else {
            System.out.println("O valor pago nao foi o suficiente");
        }
        scanner.close();
    }
}