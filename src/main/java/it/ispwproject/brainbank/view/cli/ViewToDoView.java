package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.ActivityBean;

import java.util.List;

public class ViewToDoView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Le mie attività");
    }

    public void mostraAttivita(List<ActivityBean> activities) {
        if (activities.isEmpty()) {
            CLIRenderer.messaggio("Nessuna attività assegnata.");
            return;
        }

        // ── Da completare
        CLIRenderer.sezione("Da completare");
        boolean hasPending = false;
        for (int i = 0; i < activities.size(); i++) {
            ActivityBean a = activities.get(i);
            if (!a.isCompleted()) {
                String tutorInfo = a.getTutor() != null
                        ? "  (da: " + a.getTutor().getName() + " " + a.getTutor().getSurname() + ")" : "";
                System.out.printf("  [%d] %s  %s%s%n",
                        i + 1, CLIRenderer.PENDING, a.getDescription(), tutorInfo);
                hasPending = true;
            }
        }
        if (!hasPending) CLIRenderer.messaggio("Nessuna attività in sospeso.");

        // ── Completate
        CLIRenderer.sezione("Completate");
        boolean hasCompleted = false;
        for (ActivityBean a : activities) {
            if (a.isCompleted()) {
                String tutorInfo = a.getTutor() != null
                        ? "  (da: " + a.getTutor().getName() + " " + a.getTutor().getSurname() + ")" : "";
                System.out.printf("  %s  %s%s%n",
                        CLIRenderer.DONE, a.getDescription(), tutorInfo);
                hasCompleted = true;
            }
        }
        if (!hasCompleted) CLIRenderer.messaggio("Nessuna attività completata.");
    }

    public void mostraPendingPerSelezione(List<ActivityBean> pending) {
        CLIRenderer.sezione("Segna come completata");
        for (int i = 0; i < pending.size(); i++) {
            System.out.printf("  [%d] %s  %s%n",
                    i + 1, CLIRenderer.PENDING, pending.get(i).getDescription());
        }
        CLIRenderer.voceMenuZero("Annulla");
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }
}