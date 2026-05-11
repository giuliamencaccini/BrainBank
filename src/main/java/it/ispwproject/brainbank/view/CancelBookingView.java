package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.BookingResponseBean;

import java.util.List;
import java.util.Scanner;

public class CancelBookingView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Annulla una prenotazione");
        System.out.println(SEPARATOR);
    }

    public void mostraPrenotazioniAnnullabili(List<BookingResponseBean> cancellable) {
        for (int i = 0; i < cancellable.size(); i++) {
            BookingResponseBean b = cancellable.get(i);
            System.out.printf("  [%d] ID %-3d  %s  –  %s  –  %s  %s-%s%n",
                    i + 1,
                    b.getId(),
                    b.getSubject().getName(),
                    b.getTutor().getName(),
                    b.getTimeSlot().getDate(),
                    b.getTimeSlot().getStartTime(),
                    b.getTimeSlot().getEndTime());
        }
    }

    public void mostraRiepilogo(BookingResponseBean selected) {
        System.out.println("\n  ── Conferma annullamento");
        System.out.printf("  Materia : %s%n", selected.getSubject().getName());
        System.out.printf("  Tutor   : %s%n", selected.getTutor().getName());
        System.out.printf("  Data    : %s  %s – %s%n",
                selected.getTimeSlot().getDate(),
                selected.getTimeSlot().getStartTime(),
                selected.getTimeSlot().getEndTime());
    }

    public void mostraSuccesso() {
        System.out.println("  ✓ Prenotazione annullata con successo.");
        System.out.println(SEPARATOR);
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println("  " + messaggio);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
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