package it.ispwproject.brainbank.view;

import java.util.Scanner;

public class EditProfileView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Profilo");
        System.out.println(SEPARATOR);
    }

    public void mostraMenu() {
        System.out.println("  [1] Modifica email");
        System.out.println("  [0] Indietro");
    }

    public String chiediScelta() {
        System.out.print("\n  Scelta: ");
        return scanner.nextLine().trim();
    }

    public String chiediCampo(String label) {
        System.out.printf("  %s: ", label);
        return scanner.nextLine().trim();
    }

    public void mostraSuccesso(String messaggio) {
        System.out.println("  ✓ " + messaggio);
        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println("  " + messaggio);
    }

    public boolean chiediConferma(String prompt) {
        while (true) {
            System.out.printf("  %s [s/n]: ", prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sì")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Rispondi con 's' oppure 'n'.");
        }
    }
}

