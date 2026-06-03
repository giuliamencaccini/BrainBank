package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.BookingResponseBean;

import java.util.List;

public class CancelBookingView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Annulla una prenotazione");
    }

    public void mostraPrenotazioniAnnullabili(List<BookingResponseBean> cancellable) {
        CLIRenderer.sezione("Prenotazioni attive");

        // larghezza colonne calcolata sui dati reali
        int subjectW = cancellable.stream()
                .mapToInt(b -> b.getSubject().getName().length())
                .max().orElse(10);
        int tutorW = cancellable.stream()
                .mapToInt(b -> (b.getTutor().getName() + " " + b.getTutor().getSurname()).length())
                .max().orElse(16);
        int numW = String.valueOf(cancellable.size()).length();

        String fmt = "  [%-" + numW + "d] %-" + subjectW + "s  %-" + tutorW + "s  %s  %s–%s%n";

        for (int i = 0; i < cancellable.size(); i++) {
            BookingResponseBean b = cancellable.get(i);
            System.out.printf(fmt,
                    i + 1,
                    b.getSubject().getName(),
                    b.getTutor().getName() + " " + b.getTutor().getSurname(),
                    b.getTimeSlot().getDate(),
                    b.getTimeSlot().getStartTime(),
                    b.getTimeSlot().getEndTime());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(BookingResponseBean selected) {
        CLIRenderer.sezione("Conferma annullamento");
        CLIRenderer.campo("Materia", selected.getSubject().getName());
        CLIRenderer.campo("Tutor",   selected.getTutor().getName() + " " + selected.getTutor().getSurname());
        CLIRenderer.campo("Data",    selected.getTimeSlot().getDate()
                + "  " + selected.getTimeSlot().getStartTime()
                + " – " + selected.getTimeSlot().getEndTime());
    }

    public void mostraSuccesso() {
        CLIRenderer.successo("Prenotazione annullata con successo.");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}