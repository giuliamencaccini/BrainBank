package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.*;

import java.util.List;

public class BookLessonView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Prenota una lezione");
    }

    public void mostraMaterie(List<SubjectBean> subjects) {
        CLIRenderer.sezione("Materie disponibili");
        for (int i = 0; i < subjects.size(); i++) {
            CLIRenderer.voceMenu(i + 1, subjects.get(i).getName());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraTutor(List<TutorBean> favourites, List<TutorBean> others) {
        CLIRenderer.sezione("Seleziona tutor");
        int index = 1;

        if (!favourites.isEmpty()) {
            CLIRenderer.messaggio(CLIRenderer.STAR + "  Tutor preferiti");
            for (TutorBean t : favourites) {
                System.out.printf("  [%d] %-22s  %s%n",
                        index++,
                        t.getName() + " " + t.getSurname(),
                        t.getBio() != null ? t.getBio() : "");
            }
            if (!others.isEmpty()) CLIRenderer.vuota();
        }

        if (!others.isEmpty()) {
            if (!favourites.isEmpty()) CLIRenderer.messaggio("  Altri tutor");
            for (TutorBean t : others) {
                System.out.printf("  [%d] %-22s  %s%n",
                        index++,
                        t.getName() + " " + t.getSurname(),
                        t.getBio() != null ? t.getBio() : "");
            }
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraSlot(List<TimeSlotBean> slots) {
        CLIRenderer.sezione("Slot disponibili");
        for (int i = 0; i < slots.size(); i++) {
            TimeSlotBean s = slots.get(i);
            System.out.printf("  [%d] %s   %s – %s%n",
                    i + 1, s.getDate(), s.getStartTime(), s.getEndTime());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(BookingResponseBean summary) {
        CLIRenderer.sezione("Riepilogo prenotazione");
        CLIRenderer.campo("Materia",  summary.getSubject().getName());
        CLIRenderer.campo("Tutor",    summary.getTutor().getName() + " " + summary.getTutor().getSurname());
        CLIRenderer.campo("Data",     summary.getTimeSlot().getDate().toString());
        CLIRenderer.campo("Orario",   summary.getTimeSlot().getStartTime()
                + " – " + summary.getTimeSlot().getEndTime());
    }

    public void mostraConferma(BookingResponseBean response) {
        CLIRenderer.sezione("Prenotazione confermata");
        CLIRenderer.campo("Stato",  response.getStatus() != null ? response.getStatus().toString() : "—");
        CLIRenderer.campo("Meet",   response.getMeetLink());
        CLIRenderer.separatore();
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}