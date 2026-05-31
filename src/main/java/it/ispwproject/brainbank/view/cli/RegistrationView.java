package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.enumerator.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegistrationView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Registrazione");
        System.out.println(SEPARATOR);
    }

    public String chiediCampo(String label) {
        System.out.printf("  %s: ", label);
        return scanner.nextLine().trim();
    }

    public String chiediPassword(String label) {
        System.out.printf("  %s: ", label);
        return scanner.nextLine().trim();
    }

    public Role chiediRuolo() {
        while (true) {
            System.out.println("\n  Ruolo:");
            System.out.println("  [1] Studente");
            System.out.println("  [2] Tutor");
            System.out.print("\n  Scelta [1-2]: ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return Role.STUDENT;
            if (input.equals("2")) return Role.TUTOR;
            System.out.println("  ❌ Scelta non valida.");
        }
    }

    public List<SubjectBean> chiediMaterie(List<SubjectBean> subjects) {
        System.out.println("\n  ── Materie che insegni (seleziona una o più)");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, subjects.get(i).getName());
        }

        List<SubjectBean> selected = new ArrayList<>();
        System.out.print("\n  Inserisci i numeri separati da virgola (es. 1,3): ");
        String input = scanner.nextLine().trim();

        for (String part : input.split(",")) {
            try {
                int idx = Integer.parseInt(part.trim()) - 1;
                if (idx >= 0 && idx < subjects.size()) {
                    selected.add(subjects.get(idx));
                }
            } catch (NumberFormatException e) {
                // ignora input non validi
            }
        }

        return selected;
    }

    public void mostraSuccesso() {
        System.out.println("\n  ✓ Registrazione completata! Ora puoi effettuare il login.");
        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }
}