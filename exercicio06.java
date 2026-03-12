import java.util.Scanner;

public class exercicio06 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double base;
        double altura;
        System.out.println(" Digite a base (em cm): ");
        base = scanner.nextDouble();
        System.out.println("Digite a altura (em cm): ");
        altura = scanner.nextDouble();
        double area;
        area = Math.pow(base, altura);
        System.out.printf("Area do retangulo : %.2f cm²", area);
        scanner.close();

    }
}
