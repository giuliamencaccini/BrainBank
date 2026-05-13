package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.StatisticsBean;

import java.util.Map;

public class ReportStatisticsView {

    private static final String SEPARATOR = "─".repeat(50);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – Statistiche");
        System.out.println(SEPARATOR);
    }

    public void mostraStatistiche(StatisticsBean stats) {
        System.out.printf("  Prenotazioni totali  : %d%n", stats.getTotalBookings());
        System.out.printf("  Prenotazioni annullate: %d%n", stats.getCancelledBookings());
        System.out.printf("  Tasso cancellazione  : %.1f%%%n", stats.getCancellationRate());

        System.out.println("\n  ── Top 3 Tutor");
        if (stats.getTopTutors().isEmpty()) {
            System.out.println("  Nessun dato disponibile.");
        } else {
            int i = 1;
            for (Map.Entry<String, Integer> entry : stats.getTopTutors().entrySet()) {
                System.out.printf("  [%d] %-25s %d lezioni%n", i++,
                        entry.getKey(), entry.getValue());
            }
        }

        System.out.println("\n  ── Top 3 Materie");
        if (stats.getTopSubjects().isEmpty()) {
            System.out.println("  Nessun dato disponibile.");
        } else {
            int i = 1;
            for (Map.Entry<String, Integer> entry : stats.getTopSubjects().entrySet()) {
                System.out.printf("  [%d] %-25s %d prenotazioni%n", i++,
                        entry.getKey(), entry.getValue());
            }
        }

        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }
}