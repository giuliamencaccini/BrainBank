package it.ispwproject.brainbank.view;

import java.util.Scanner;

public class LoginView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public String[] chiediCredenziali() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Login");
        System.out.println(SEPARATOR);

        System.out.print("  Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("  Password: ");
        String password = scanner.nextLine().trim();

        return new String[]{email, password};
    }

    public void mostraErroreInput() {
        System.out.println("  ❌ Inserisci sia email che password.");
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }

    public void mostraSuccesso(String nome) {
        System.out.println("  ✓ Benvenuto " + nome + "!");
    }
}