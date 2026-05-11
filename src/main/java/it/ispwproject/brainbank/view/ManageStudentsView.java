package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.ActivityBean;
import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.bean.ProgressBean;
import it.ispwproject.brainbank.bean.StudentBean;

import java.util.List;
import java.util.Scanner;

public class ManageStudentsView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Gestisci studenti");
        System.out.println(SEPARATOR);
    }

    public void mostraStudenti(List<StudentBean> students) {
        if (students.isEmpty()) {
            System.out.println("  Nessuno studente ha ancora prenotato con te.");
            return;
        }
        System.out.println("\n  ── I tuoi studenti");
        for (int i = 0; i < students.size(); i++) {
            StudentBean s = students.get(i);
            System.out.printf("  [%d] %s  (%s)%n", i + 1, s.getFullName(), s.getEmail());
        }
    }

    public void mostraSchedaStudente(StudentBean student,
                                     List<BookingResponseBean> completedLessons,
                                     List<BookingResponseBean> upcomingLessons,
                                     ProgressBean progress) {
        System.out.println("\n" + SEPARATOR);
        System.out.printf("  Studente: %s%n", student.getFullName());
        System.out.println(SEPARATOR);

        // Lezioni effettuate
        System.out.println("\n  ── Lezioni effettuate");
        if (completedLessons.isEmpty()) {
            System.out.println("  Nessuna lezione ancora effettuata.");
        } else {
            for (BookingResponseBean b : completedLessons) {
                System.out.printf("  • %s  %s  %s – %s%n",
                        b.getTimeSlot().getDate(),
                        b.getSubject().getName(),
                        b.getTimeSlot().getStartTime(),
                        b.getTimeSlot().getEndTime());
            }
        }

        // Lezioni programmate
        System.out.println("\n  ── Lezioni programmate");
        if (upcomingLessons.isEmpty()) {
            System.out.println("  Nessuna lezione programmata.");
        } else {
            for (BookingResponseBean b : upcomingLessons) {
                System.out.printf("  • %s  %s  %s – %s%n",
                        b.getTimeSlot().getDate(),
                        b.getSubject().getName(),
                        b.getTimeSlot().getStartTime(),
                        b.getTimeSlot().getEndTime());
                if (b.getMeetLink() != null) {
                    System.out.println("    Link: " + b.getMeetLink());
                }
            }
        }

        // Progressi
        System.out.println("\n  ── Progressi");
        if (progress == null) {
            System.out.println("  Nessun progresso annotato.");
        } else {
            System.out.println("  " + progress.getNotes());
            System.out.println("  Ultimo aggiornamento: " + progress.getUpdatedAt().toLocalDate());
        }
    }

    public void mostraMenuStudente() {
        System.out.println("\n  ── Azioni");
        System.out.println("  [1] Annota progressi");
        System.out.println("  [2] Assegna attività");
        System.out.println("  [3] Visualizza attività assegnate");
        System.out.println("  [0] Torna alla lista");
    }

    public void mostraAttivita(List<ActivityBean> activities) {
        System.out.println("\n  ── Attività assegnate");
        if (activities.isEmpty()) {
            System.out.println("  Nessuna attività assegnata.");
        } else {
            for (ActivityBean a : activities) {
                String stato = a.isCompleted() ? "✓ Completata" : "✗ In sospeso";
                System.out.printf("  [%s] %s  (%s)%n",
                        stato, a.getDescription(),
                        a.getCreatedAt().toLocalDate());
            }
        }
    }

    public void mostraSuccesso(String messaggio) {
        System.out.println("  ✓ " + messaggio);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
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

    public String chiediTesto(String prompt) {
        System.out.printf("  %s: ", prompt);
        return scanner.nextLine().trim();
    }
}