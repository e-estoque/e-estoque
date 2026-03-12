import java.util.Scanner;

public class exercicio10 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double distancia;
        double velocidadeMedia;
        System.out.println("Digite a distancia(km): ");
        distancia = scanner.nextDouble();
        System.out.println("Digite a velocidade media (km/h) ");
        velocidadeMedia = scanner.nextDouble();
        double tempoHoras;
        tempoHoras = distancia / velocidadeMedia;
        int horas = (int) tempoHoras;
        int minutos = (int) ((tempoHoras - horas) * 60);
        System.out.printf("Seu tempo estimado é de : %d horas e %d minutos", horas, minutos);

        scanner.close();

    }
}
