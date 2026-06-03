package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.StatisticsBean;

import java.util.Map;

public class ReportStatisticsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Statistiche e report");
    }

    public void mostraStatistiche(StatisticsBean stats) {
        CLIRenderer.sezione("Prenotazioni");
        CLIRenderer.campo("Totali",    String.valueOf(stats.getTotalBookings()));
        CLIRenderer.campo("Annullate", String.valueOf(stats.getCancelledBookings()));
        CLIRenderer.campo("Tasso",     String.format("%.1f%%", stats.getCancellationRate()));

        CLIRenderer.sezione("Top 3 Tutor");
        if (stats.getTopTutors().isEmpty()) {
            CLIRenderer.messaggio("Nessun dato disponibile.");
        } else {
            int i = 1;
            for (Map.Entry<String, Integer> e : stats.getTopTutors().entrySet()) {
                System.out.printf("  %d.  %-28s  %d lezioni%n",
                        i++, e.getKey(), e.getValue());
            }
        }

        CLIRenderer.sezione("Top 3 Materie");
        if (stats.getTopSubjects().isEmpty()) {
            CLIRenderer.messaggio("Nessun dato disponibile.");
        } else {
            int i = 1;
            for (Map.Entry<String, Integer> e : stats.getTopSubjects().entrySet()) {
                System.out.printf("  %d.  %-28s  %d prenotazioni%n",
                        i++, e.getKey(), e.getValue());
            }
        }

        CLIRenderer.separatore();
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}