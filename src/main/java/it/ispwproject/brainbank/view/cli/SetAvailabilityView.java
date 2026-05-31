package it.ispwproject.brainbank.view.cli;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class SetAvailabilityView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Imposta disponibilità");
        System.out.println(SEPARATOR);
    }

    public LocalDate chiediData() {
        while (true) {
            System.out.print("  Data (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("  ❌ Formato non valido. Usa YYYY-MM-DD.");
            }
        }
    }

    public LocalTime chiediOra(String label) {
        while (true) {
            System.out.printf("  %s (HH:MM): ", label);
            try {
                return LocalTime.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("  ❌ Formato non valido. Usa HH:MM.");
            }
        }
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

    public void mostraSuccesso() {
        System.out.println("  ✓ Slot aggiunto con successo.");
        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println("  " + messaggio);
    }
}