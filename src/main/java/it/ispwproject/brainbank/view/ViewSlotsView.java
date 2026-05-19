package it.ispwproject.brainbank.view;

import it.ispwproject.brainbank.bean.TimeSlotBean;

import java.util.List;
import java.util.Map;

public class ViewSlotsView {

    private static final String SEPARATOR = "─".repeat(50);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – I miei slot");
        System.out.println(SEPARATOR);
    }

    public void mostraSlots(List<TimeSlotBean> slots, Map<Integer, String> subjectBySlot) {
        if (slots.isEmpty()) {
            System.out.println("  Non hai ancora slot.");
        } else {
            for (TimeSlotBean s : slots) {
                if (s.isAvailable()) {
                    System.out.printf("  %s  %s – %s  [✓ Disponibile]%n",
                            s.getDate(), s.getStartTime(), s.getEndTime());
                } else {
                    String nome = s.getBookedByName() != null ? s.getBookedByName() : "Studente";
                    String materia = subjectBySlot.getOrDefault(s.getId(), "—");
                    System.out.printf("  %s  %s – %s  [✗ Prenotato]%n",
                            s.getDate(), s.getStartTime(), s.getEndTime());
                    System.out.println("         Materia  : " + materia);
                    System.out.println("         Studente : " + nome);
                    if (s.getMeetLink() != null)
                        System.out.println("         Link     : " + s.getMeetLink());
                }
            }
        }
        System.out.println(SEPARATOR);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }
}