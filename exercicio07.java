import java.util.Scanner;

public class exercicio07 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Double distancia;
        double combustivel;
        System.out.println("Digite a distancia percorrida (km): ");
        distancia = scanner.nextDouble();
        System.out.println("Digite o combustivel gasto(litros): ");
        combustivel = scanner.nextDouble();
        double consumo;
        consumo = distancia / combustivel;
        System.out.printf("Consumo medio: %.2f km/l", consumo);

        scanner.close();
    }
}