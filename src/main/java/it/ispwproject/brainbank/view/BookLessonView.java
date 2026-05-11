package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.*;

import java.util.List;
import java.util.Scanner;

public class BookLessonView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Prenota una lezione");
        System.out.println(SEPARATOR);
    }

    public void mostraMaterie(List<SubjectBean> subjects) {
        System.out.println("\n  ── Materie disponibili");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, subjects.get(i).getName());
        }
    }

    public void mostraTutor(List<TutorBean> favourites, List<TutorBean> others) {
        System.out.println("\n  ── Seleziona tutor");
        int index = 1;

        if (!favourites.isEmpty()) {
            System.out.println("  ★ Tutor preferiti");
            for (TutorBean t : favourites) {
                System.out.printf("  [%d] %-20s  %s%n", index++, t.getName(), t.getBio());
            }
            if (!others.isEmpty()) System.out.println();
        }

        if (!others.isEmpty()) {
            if (!favourites.isEmpty()) System.out.println("  Altri tutor");
            for (TutorBean t : others) {
                System.out.printf("  [%d] %-20s  %s%n", index++, t.getName(), t.getBio());
            }
        }
    }

    public void mostraSlot(List<TimeSlotBean> slots) {
        System.out.println("\n  ── Slot disponibili");
        for (int i = 0; i < slots.size(); i++) {
            TimeSlotBean s = slots.get(i);
            System.out.printf("  [%d] %s   %s – %s%n",
                    i + 1, s.getDate(), s.getStartTime(), s.getEndTime());
        }
    }

    public void mostraRiepilogo(BookingResponseBean summary) {
        System.out.println("\n  ── Riepilogo prenotazione");
        System.out.printf("  Materia  : %s%n", summary.getSubject().getName());
        System.out.printf("  Tutor    : %s%n", summary.getTutor().getName());
        System.out.printf("  Data     : %s%n", summary.getTimeSlot().getDate());
        System.out.printf("  Orario   : %s – %s%n",
                summary.getTimeSlot().getStartTime(), summary.getTimeSlot().getEndTime());
    }

    public void mostraConferma(BookingResponseBean response) {
        System.out.println("\n  ── Prenotazione confermata");
        System.out.printf("  ID       : %d%n", response.getId());
        System.out.printf("  Stato    : %s%n", response.getStatus());
        System.out.printf("  Meet     : %s%n", response.getMeetLink());
        System.out.println(SEPARATOR);
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println("  " + messaggio);
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