package it.ispwproject.brainbank.view;

import java.util.Scanner;

public class DashboardTutorView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraBenvenuto(String nome) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.printf("  Bentornato %s!  –  Tutor%n", nome);
        System.out.println(SEPARATOR);
    }

    public void mostraMenu() {
        System.out.println("  [1] Disponibilità");
        System.out.println("  [2] I miei slot");
        System.out.println("  [3] Gestisci studenti");
        System.out.println("  [0] Logout");
    }

    public String chiediScelta() {
        System.out.print("\n  Scelta: ");
        return scanner.nextLine().trim();
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println("  " + messaggio);
    }
}