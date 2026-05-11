package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.TimeSlotBean;

import java.util.List;

public class ViewSlotsView {

    private static final String SEPARATOR = "─".repeat(50);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – I miei slot");
        System.out.println(SEPARATOR);
    }

    public void mostraSlots(List<TimeSlotBean> slots) {
        if (slots.isEmpty()) {
            System.out.println("  Non hai ancora slot.");
        } else {
            for (TimeSlotBean s : slots) {
                if (s.isAvailable()) {
                    System.out.printf("  ID %-3d  %s  %s – %s  [✓ Disponibile]%n",
                            s.getId(), s.getDate(),
                            s.getStartTime(), s.getEndTime());
                } else {
                    String nome = s.getBookedByName() != null ? s.getBookedByName() : "Studente";
                    System.out.printf("  ID %-3d  %s  %s – %s  [✗ Prenotato da: %s]%n",
                            s.getId(), s.getDate(),
                            s.getStartTime(), s.getEndTime(), nome);
                    if (s.getMeetLink() != null) {
                        System.out.println("         Link: " + s.getMeetLink());
                    }
                }
            }
        }
        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }
}