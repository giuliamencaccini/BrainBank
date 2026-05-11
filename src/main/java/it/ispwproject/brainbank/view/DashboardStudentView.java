package it.ispwproject.brainbank.view;

import java.util.Scanner;

public class DashboardStudentView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraBenvenuto(String nome) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.printf("  Bentornato %s!  –  Studente%n", nome);
        System.out.println(SEPARATOR);
    }

    public void mostraMenu() {
        System.out.println("  [1] Prenota una lezione");
        System.out.println("  [2] Le mie prenotazioni");
        System.out.println("  [3] Annulla una prenotazione");
        System.out.println("  [4] To-do");
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