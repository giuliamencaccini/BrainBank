package it.ispwproject.brainbank.view;

import java.util.Scanner;

public class ModeSelectorView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraMenu() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank — Seleziona modalità");
        System.out.println(SEPARATOR);
        System.out.println("  [1] Demo     (in-memory, nessun DB richiesto)");
        System.out.println("  [2] Database (persistenza MySQL)");
        System.out.println("  [3] File     (persistenza JSON)");
        System.out.println("  [0] Esci");
        System.out.println(SEPARATOR);
    }

    public String chiediScelta() {
        System.out.print("\n  Scelta: ");
        return scanner.nextLine().trim();
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }

    public void mostraModalitaSelezionata(String modalita) {
        System.out.println("  ✓ Modalità selezionata: " + modalita);
    }
}