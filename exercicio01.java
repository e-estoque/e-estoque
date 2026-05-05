public class exercicio01 {
    public static void main(String[] args) {

        double numero = 10.1234567;
        System.out.println(numero);
        System.out.println("O numero é igual a: " + numero); // ou posso usar:
        System.out.printf("RESULTADO = %.2f metros%n", numero);
        System.out.printf("%.2f%n", numero);
        String nome = "Julia";
        int idade = 19;
        double salario = 1500;
        System.out.printf("%s tem %d anos e recebe %f", nome, idade, salario);
        System.out.println("Aqui eu nao indiquei quantas casas logo ele me deu todas, e nem pedi pra ele pular linha");
        System.out.printf("%s tem %d e recebe %.2f reais por mes %n", nome, idade, salario);

    }
}