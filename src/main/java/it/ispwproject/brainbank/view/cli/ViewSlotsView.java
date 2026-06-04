package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.TimeSlotBean;

import java.util.List;
import java.util.Map;

public class ViewSlotsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  I miei slot");
    }

    public void mostraTab(int nDisponibili, int nPrenotati, int nPassati) {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Disponibili  (" + nDisponibili + ")");
        CLIRenderer.voceMenu(2, "Prenotati    (" + nPrenotati + ")");
        CLIRenderer.voceMenu(3, "Scaduti      (" + nPassati + ")");
        CLIRenderer.voceMenu(4, "Elimina slot");
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraDisponibili(List<TimeSlotBean> disponibili) {
        CLIRenderer.sezione("Slot disponibili  (" + disponibili.size() + ")");
        if (disponibili.isEmpty()) {
            CLIRenderer.messaggio("Nessuno slot disponibile.");
        } else {
            for (TimeSlotBean s : disponibili) {
                System.out.printf("  %s  %s – %s    %s Disponibile%n",
                        s.getDate(), s.getStartTime(), s.getEndTime(), CLIRenderer.AVAIL);
            }
        }
        CLIRenderer.separatore();
    }

    public void mostraPrenotati(List<TimeSlotBean> prenotati, Map<Integer, String> subjectBySlot) {
        CLIRenderer.sezione("Slot prenotati  (" + prenotati.size() + ")");
        if (prenotati.isEmpty()) {
            CLIRenderer.messaggio("Nessuno slot prenotato.");
        } else {
            for (TimeSlotBean s : prenotati) {
                String nome    = s.getBookedByName() != null ? s.getBookedByName() : "Studente";
                String materia = subjectBySlot.getOrDefault(s.getId(), "—");
                System.out.printf("  %s  %s – %s    %s Prenotato  |  %s  –  %s%n",
                        s.getDate(), s.getStartTime(), s.getEndTime(),
                        CLIRenderer.BOOKED, materia, nome);
                if (s.getMeetLink() != null)
                    CLIRenderer.campo("  Link", s.getMeetLink());
            }
        }
        CLIRenderer.separatore();
    }

    public void mostraPassati(List<TimeSlotBean> passati, Map<Integer, String> subjectBySlot) {
        if (passati.isEmpty()) {
            CLIRenderer.messaggio("Nessuno slot scaduto.");
            CLIRenderer.separatore();
            return;
        }

        CLIRenderer.sezione("Slot scaduti  (" + passati.size() + ")");
        for (TimeSlotBean s : passati) {
            String simbolo = s.isAvailable() ? CLIRenderer.AVAIL : CLIRenderer.BOOKED;
            String stato   = s.isAvailable() ? "Non utilizzato" : "Utilizzato";
            String extra   = "";
            if (!s.isAvailable()) {
                String materia = subjectBySlot.getOrDefault(s.getId(), "—");
                String nome    = s.getBookedByName() != null ? s.getBookedByName() : "Studente";
                extra = String.format("  |  %-12s  –  %s", materia, nome);
            }
            System.out.printf("  %s  %s – %s    %s %s%s%n",
                    s.getDate(), s.getStartTime(), s.getEndTime(), simbolo, stato, extra);
        }
        CLIRenderer.separatore();
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