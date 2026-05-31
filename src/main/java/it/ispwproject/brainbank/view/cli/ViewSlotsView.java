package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.TimeSlotBean;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ViewSlotsView {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("  BrainBank – I miei slot");
        System.out.println(SEPARATOR);
    }

    public void mostraSlots(List<TimeSlotBean> slots, Map<Integer, String> subjectBySlot) {
        List<TimeSlotBean> disponibili = slots.stream().filter(TimeSlotBean::isAvailable).toList();
        List<TimeSlotBean> prenotati   = slots.stream().filter(s -> !s.isAvailable()).toList();

        System.out.println("\n  ── Slot futuri disponibili (" + disponibili.size() + ")");
        if (disponibili.isEmpty()) {
            System.out.println("  Nessuno slot disponibile.");
        } else {
            for (TimeSlotBean s : disponibili) {
                System.out.printf("  %s  %s – %s  [✓ Disponibile]%n",
                        s.getDate(), s.getStartTime(), s.getEndTime());
            }
        }

        System.out.println("\n  ── Slot futuri prenotati (" + prenotati.size() + ")");
        if (prenotati.isEmpty()) {
            System.out.println("  Nessuno slot prenotato.");
        } else {
            for (TimeSlotBean s : prenotati) {
                String nome    = s.getBookedByName() != null ? s.getBookedByName() : "Studente";
                String materia = subjectBySlot.getOrDefault(s.getId(), "—");
                System.out.printf("  %s  %s – %s  [✗ Prenotato]%n",
                        s.getDate(), s.getStartTime(), s.getEndTime());
                System.out.println("         Materia  : " + materia);
                System.out.println("         Studente : " + nome);
                if (s.getMeetLink() != null)
                    System.out.println("         Link     : " + s.getMeetLink());
            }
        }
        System.out.println(SEPARATOR);
    }

    public void mostraPassati(List<TimeSlotBean> passati, Map<Integer, String> subjectBySlot) {
        if (passati.isEmpty()) return;

        System.out.println("\n  ── Slot scaduti (" + passati.size() + ")");
        for (TimeSlotBean s : passati) {
            String stato = s.isAvailable() ? "[○ Non utilizzato]" : "[● Utilizzato]";
            System.out.printf("  %s  %s – %s  %s%n",
                    s.getDate(), s.getStartTime(), s.getEndTime(), stato);
        }
        System.out.println(SEPARATOR);
    }

    public boolean chiediEliminazioneSlot() {
        while (true) {
            System.out.print("\n  Vuoi eliminare uno slot disponibile? [s/n]: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sì")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Rispondi con 's' oppure 'n'.");
        }
    }

    public void mostraSlotDisponibili(List<TimeSlotBean> disponibili) {
        System.out.println("\n  ── Slot disponibili eliminabili");
        for (int i = 0; i < disponibili.size(); i++) {
            TimeSlotBean s = disponibili.get(i);
            System.out.printf("  [%d] %s  %s – %s%n",
                    i + 1, s.getDate(), s.getStartTime(), s.getEndTime());
        }
    }

    public int chiediScelta(String prompt, int min, int max) {
        while (true) {
            System.out.printf("%n  %s [%d-%d]: ", prompt, min, max);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.printf("  Inserisci un numero tra %d e %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Input non valido.");
            }
        }
    }

    public boolean chiediConferma(String prompt) {
        while (true) {
            System.out.printf("  %s [s/n]: ", prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sì")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Rispondi con 's' oppure 'n'.");
        }
    }

    public void mostraSuccessoEliminazione() {
        System.out.println("  ✓ Slot eliminato con successo.");
        System.out.println(SEPARATOR);
    }

    public void mostraMessaggio(String messaggio) {
        System.out.println("  " + messaggio);
    }

    public void mostraErrore(String messaggio) {
        System.out.println("  ❌ " + messaggio);
    }
}