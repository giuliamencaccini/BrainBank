package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.enumerator.Role;

import java.util.ArrayList;
import java.util.List;

public class RegistrationView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Registrazione");
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public String chiediPassword(String label) {
        return CLIRenderer.chiediCampo(label);   // in CLI il testo resta visibile
    }

    public Role chiediRuolo() {
        while (true) {
            CLIRenderer.sezione("Ruolo");
            CLIRenderer.voceMenu(1, "Studente");
            CLIRenderer.voceMenu(2, "Tutor");
            String input = CLIRenderer.chiediSceltaStringa("Scelta [1-2]");
            if (input.equals("1")) return Role.STUDENT;
            if (input.equals("2")) return Role.TUTOR;
            CLIRenderer.errore("Scelta non valida.");
        }
    }

    public List<SubjectBean> chiediMaterie(List<SubjectBean> subjects) {
        CLIRenderer.sezione("Materie che insegni  (seleziona una o più)");
        for (int i = 0; i < subjects.size(); i++) {
            CLIRenderer.voceMenu(i + 1, subjects.get(i).getName());
        }

        List<SubjectBean> selected = new ArrayList<>();
        System.out.print("\n  Numeri separati da virgola (es. 1,3): ");
        String input = CLIRenderer.SCANNER.nextLine().trim();

        for (String part : input.split(",")) {
            try {
                int idx = Integer.parseInt(part.trim()) - 1;
                if (idx >= 0 && idx < subjects.size()) selected.add(subjects.get(idx));
            } catch (NumberFormatException e) {
                // ignora token non numerici
            }
        }
        return selected;
    }

    public void mostraSuccesso() {
        CLIRenderer.vuota();
        CLIRenderer.successo("Registrazione completata! Ora puoi effettuare il login.");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}