package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.TimeSlotBean;

import java.util.List;
import java.util.Map;

public class ViewSlotsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  I miei slot");
    }

    public void mostraSlots(List<TimeSlotBean> slots, Map<Integer, String> subjectBySlot) {
        List<TimeSlotBean> disponibili = slots.stream().filter(TimeSlotBean::isAvailable).toList();
        List<TimeSlotBean> prenotati   = slots.stream().filter(s -> !s.isAvailable()).toList();

        // ── Slot futuri disponibili
        CLIRenderer.sezione("Slot futuri disponibili  (" + disponibili.size() + ")");
        if (disponibili.isEmpty()) {
            CLIRenderer.messaggio("Nessuno slot disponibile.");
        } else {
            for (TimeSlotBean s : disponibili) {
                System.out.printf("  %s  %s – %s    %s Disponibile%n",
                        s.getDate(), s.getStartTime(), s.getEndTime(), CLIRenderer.AVAIL);
            }
        }

        // ── Slot futuri prenotati
        CLIRenderer.sezione("Slot futuri prenotati  (" + prenotati.size() + ")");
        if (prenotati.isEmpty()) {
            CLIRenderer.messaggio("Nessuno slot prenotato.");
        } else {
            for (TimeSlotBean s : prenotati) {
                String nome    = s.getBookedByName() != null ? s.getBookedByName() : "Studente";
                String materia = subjectBySlot.getOrDefault(s.getId(), "—");
                System.out.printf("  %s  %s – %s    %s Prenotato%n",
                        s.getDate(), s.getStartTime(), s.getEndTime(), CLIRenderer.BOOKED);
                CLIRenderer.campo("  Materia",   materia);
                CLIRenderer.campo("  Studente",  nome);
                if (s.getMeetLink() != null)
                    CLIRenderer.campo("  Link",  s.getMeetLink());
            }
        }
        CLIRenderer.separatore();
    }

    public void mostraPassati(List<TimeSlotBean> passati) {
        if (passati.isEmpty()) return;

        CLIRenderer.sezione("Slot scaduti  (" + passati.size() + ")");
        for (TimeSlotBean s : passati) {
            String simbolo = s.isAvailable() ? CLIRenderer.AVAIL : CLIRenderer.BOOKED;
            String stato   = s.isAvailable() ? "Non utilizzato" : "Utilizzato";
            System.out.printf("  %s  %s – %s    %s %s%n",
                    s.getDate(), s.getStartTime(), s.getEndTime(), simbolo, stato);
        }
        CLIRenderer.separatore();
    }

    public boolean chiediEliminazioneSlot() {
        return CLIRenderer.chiediConferma("Vuoi eliminare uno slot disponibile?");
    }

    public void mostraSlotDisponibili(List<TimeSlotBean> disponibili) {
        CLIRenderer.sezione("Slot disponibili eliminabili");
        for (int i = 0; i < disponibili.size(); i++) {
            TimeSlotBean s = disponibili.get(i);
            System.out.printf("  [%d] %s  %s – %s%n",
                    i + 1, s.getDate(), s.getStartTime(), s.getEndTime());
        }
        CLIRenderer.voceMenuZero("Annulla");
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void mostraSuccessoEliminazione() {
        CLIRenderer.successo("Slot eliminato con successo.");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}