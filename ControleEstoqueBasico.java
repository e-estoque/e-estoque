import java.util.Scanner;

public class ControleEstoqueBasico {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcaoPrincipal;

        do {
            System.out.println("\n===== CONTROLE DE ESTOQUE =====");
            System.out.println("1 - Lojista");
            System.out.println("2 - Cliente");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");
            opcaoPrincipal = lerInteiro(sc);

            switch (opcaoPrincipal) {
                case 1:
                    menuLojista(sc);
                    break;
                case 2:
                    menuCliente(sc);
                    break;
                case 0:
                    System.out.println("Sistema encerrado.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        } while (opcaoPrincipal != 0);

        sc.close();
    }

    public static void menuLojista(Scanner sc) {
        String loginCorreto = "lojista";
        String senhaCorreta = "1234";

        System.out.print("\nLogin: ");
        String login = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();

        if (!login.equals(loginCorreto) || !senha.equals(senhaCorreta)) {
            System.out.println("Login ou senha incorretos.");
            return;
        }

        int opcaoLojista;

        do {
            System.out.println("\n===== MENU DO LOJISTA =====");
            System.out.println("1 - Adicionar produtos");
            System.out.println("2 - Remover produtos");
            System.out.println("3 - Consultar estoque");
            System.out.println("4 - Acompanhar gastos mensais");
            System.out.println("5 - Acompanhar gastos previstos");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");
            opcaoLojista = lerInteiro(sc);

            switch (opcaoLojista) {
                case 1:
                    System.out.println("Opcao adicionar produtos em desenvolvimento.");
                    break;
                case 2:
                    System.out.println("Opcao remover produtos em desenvolvimento.");
                    break;
                case 3:
                    System.out.println("Opcao consultar estoque em desenvolvimento.");
                    break;
                case 4:
                    System.out.println("Opcao gastos mensais em desenvolvimento.");
                    break;
                case 5:
                    System.out.println("Opcao gastos previstos em desenvolvimento.");
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        } while (opcaoLojista != 0);
    }

    public static void menuCliente(Scanner sc) {
        int opcaoCliente;

        do {
            System.out.println("\n===== MENU DO CLIENTE =====");
            System.out.println("1 - Consultar disponibilidade");
            System.out.println("2 - Consultar valores");
            System.out.println("3 - Comprar produto");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opcao: ");
            opcaoCliente = lerInteiro(sc);

            switch (opcaoCliente) {
                case 1:
                    System.out.println("Opcao consultar disponibilidade em desenvolvimento.");
                    break;
                case 2:
                    System.out.println("Opcao consultar valores em desenvolvimento.");
                    break;
                case 3:
                    System.out.println("Para comprar, entre em contato com o vendedor: (11) 99999-9999");
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        } while (opcaoCliente != 0);
    }

    public static int lerInteiro(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Digite um numero inteiro valido: ");
            sc.nextLine();
        }

        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }
}
