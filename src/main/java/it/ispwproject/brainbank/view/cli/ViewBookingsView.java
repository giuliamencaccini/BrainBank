package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.BookingResponseBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewBookingsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Le mie prenotazioni");
    }

    public void mostraTab(int nConfermate, int nCancellate, int nScadute) {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Confermate   (" + nConfermate + ")");
        CLIRenderer.voceMenu(2, "Cancellate   (" + nCancellate + ")");
        CLIRenderer.voceMenu(3, "Scadute      (" + nScadute + ")");
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraConfermate(List<BookingResponseBean> bookings) {
        CLIRenderer.sezione("Confermate");
        if (bookings.isEmpty()) {
            CLIRenderer.messaggio("Non hai prenotazioni confermate.");
            CLIRenderer.separatore();
            return;
        }
        for (BookingResponseBean b : bookings) {
            CLIRenderer.vuota();
            System.out.println("  " + CLIRenderer.LINE_THIN);
            CLIRenderer.campo("Materia", b.getSubject().getName());
            CLIRenderer.campo("Tutor",   b.getTutor().getName() + " " + b.getTutor().getSurname());
            CLIRenderer.campo("Data",    b.getTimeSlot().getDate()
                    + "  " + b.getTimeSlot().getStartTime()
                    + " – " + b.getTimeSlot().getEndTime());
            if (b.getTutor().getEmail() != null)
                CLIRenderer.campo("Email", b.getTutor().getEmail());
            if (b.getMeetLink() != null)
                CLIRenderer.campo("Meet",  b.getMeetLink());
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraCancellate(List<BookingResponseBean> bookings) {
        CLIRenderer.sezione("Cancellate");
        if (bookings.isEmpty()) {
            CLIRenderer.messaggio("Non hai prenotazioni cancellate.");
            CLIRenderer.separatore();
            return;
        }
        for (BookingResponseBean b : bookings) {
            CLIRenderer.vuota();
            System.out.println("  " + CLIRenderer.LINE_THIN);
            CLIRenderer.campo("Materia", b.getSubject().getName());
            CLIRenderer.campo("Tutor",   b.getTutor().getName() + " " + b.getTutor().getSurname());
            CLIRenderer.campo("Data",    b.getTimeSlot().getDate()
                    + "  " + b.getTimeSlot().getStartTime()
                    + " – " + b.getTimeSlot().getEndTime());
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraScadute(List<BookingResponseBean> past) {
        CLIRenderer.sezione("Scadute");
        if (past.isEmpty()) {
            CLIRenderer.messaggio("Nessuna lezione scaduta.");
            CLIRenderer.separatore();
            return;
        }

        // raggruppa per materia, come fa la GUI
        Map<String, List<BookingResponseBean>> bySubject = past.stream()
                .collect(Collectors.groupingBy(b -> b.getSubject().getName()));

        List<String> subjects = bySubject.keySet().stream().sorted().toList();

        for (String subject : subjects) {
            List<BookingResponseBean> group = bySubject.get(subject).stream()
                    .sorted((a, b) -> b.getTimeSlot().getDate().compareTo(a.getTimeSlot().getDate()))
                    .toList();

            CLIRenderer.vuota();
            System.out.printf("  %s  (%d %s)%n",
                    subject, group.size(), group.size() == 1 ? "lezione" : "lezioni");
            System.out.println("  " + CLIRenderer.LINE_THIN);

            for (BookingResponseBean b : group) {
                System.out.printf("  %s  %s  %s – %s  %s%n",
                        CLIRenderer.BULLET,
                        b.getTimeSlot().getDate(),
                        b.getTimeSlot().getStartTime(),
                        b.getTimeSlot().getEndTime(),
                        b.getTutor().getName() + " " + b.getTutor().getSurname());
            }
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}