package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.ActivityBean;

import java.util.List;
import java.util.Scanner;

public class ViewToDoView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Le mie attività");
        System.out.println(SEPARATOR);
    }

    public void mostraAttivita(List<ActivityBean> activities) {
        if (activities.isEmpty()) {
            System.out.println("  Nessuna attività assegnata.");
            return;
        }

        System.out.println("\n  ── Da completare");
        boolean hasPending = false;
        for (int i = 0; i < activities.size(); i++) {
            ActivityBean a = activities.get(i);
            if (!a.isCompleted()) {
                String tutorInfo = a.getTutor() != null
                        ? " (da: " + a.getTutor().getName() + " " + a.getTutor().getSurname() + ")" : "";
                System.out.printf("  [%d] ✗ %s%s%n", i + 1, a.getDescription(), tutorInfo);
                hasPending = true;
            }
        }
        if (!hasPending) System.out.println("  Nessuna attività in sospeso.");

        System.out.println("\n  ── Completate");
        boolean hasCompleted = false;
        for (ActivityBean a : activities) {
            if (a.isCompleted()) {
                String tutorInfo = a.getTutor() != null
                        ? " (da: " + a.getTutor().getName() + " " + a.getTutor().getSurname() + ")" : "";
                System.out.printf("  ✓ %s%s%n", a.getDescription(), tutorInfo);
                hasCompleted = true;
            }
        }
        if (!hasCompleted) System.out.println("  Nessuna attività completata.");
    }

    public void mostraPendingPerSelezione(List<ActivityBean> pending) {
        System.out.println("\n  ── Segna come completata");
        for (int i = 0; i < pending.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, pending.get(i).getDescription());
        }
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

    public int chiediScelta(String prompt, int min, int max) {
        while (true) {
            System.out.printf("%n  %s [%d-%d]: ", prompt, min, max);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.printf("  Inserisci un numero tra %d e %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Input non valido.");
            }
        }
    }
}