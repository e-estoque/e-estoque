import java.util.Scanner;

public class exercicio08 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double celsius;
        double fahrenheit;
        double kelvin;
        System.out.println("Digite a temperatura em Celsius: ");
        celsius = scanner.nextDouble();
        fahrenheit = (celsius * 9 / 5) + 32;
        System.out.printf("Fahrenhit: %.2f%n", fahrenheit);
        kelvin = celsius + 273.15;
        System.out.printf("Kelvin: %.2f", kelvin);

        scanner.close();

    }
}