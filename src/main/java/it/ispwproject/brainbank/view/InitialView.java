package it.ispwproject.brainbank.view;

import java.util.Scanner;

public class InitialView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraBenvenuto() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  Benvenuto in BrainBank!");
        System.out.println("  La piattaforma per prenotare lezioni con i tutor.");
        System.out.println(SEPARATOR);
    }

    public void mostraMenu() {
        System.out.println("  [1] Accedi");
        System.out.println("  [2] Registrati");
        System.out.println("  [0] Esci");
    }

    public String chiediScelta() {
        System.out.print("\n  Scelta: ");
        return scanner.nextLine().trim();
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }

    public void mostraArrivederci() {
        System.out.println("\n  Arrivederci!");
    }
}