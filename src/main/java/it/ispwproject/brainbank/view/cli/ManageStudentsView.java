package it.ispwproject.brainbank.view.cli;

import it.ispwproject.brainbank.bean.ActivityBean;
import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.bean.ProgressBean;
import it.ispwproject.brainbank.bean.StudentBean;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageStudentsView {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm");

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Gestisci studenti");
    }

    public void mostraStudenti(List<StudentBean> students) {
        if (students.isEmpty()) {
            CLIRenderer.messaggio("Nessuno studente ha ancora prenotato con te.");
            return;
        }
        CLIRenderer.sezione("I tuoi studenti");
        for (int i = 0; i < students.size(); i++) {
            StudentBean s = students.get(i);
            System.out.printf("  [%d] %-24s  %s%n", i + 1, s.getFullName(), s.getEmail());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraSchedaStudente(StudentBean student,
                                     List<BookingResponseBean> upcomingLessons,
                                     ProgressBean progress) {
        CLIRenderer.vuota();
        System.out.println(CLIRenderer.LINE_DECO);
        System.out.printf("  Studente: %s%n", student.getFullName());

        // prossima lezione in evidenza
        if (!upcomingLessons.isEmpty()) {
            BookingResponseBean next = upcomingLessons.get(0);
            System.out.printf("  %s Prossima lezione: %s  –  %s  %s–%s%n",
                    CLIRenderer.CLOCK,
                    next.getTimeSlot().getDate(),
                    next.getSubject().getName(),
                    next.getTimeSlot().getStartTime(),
                    next.getTimeSlot().getEndTime());
        }
        System.out.println(CLIRenderer.LINE_DECO);

        // progressi
        CLIRenderer.sezione("Progressi");
        if (progress == null) {
            CLIRenderer.messaggio("Nessun progresso annotato.");
        } else {
            // gestione multiriga
            for (String riga : progress.getNotes().split("\n")) {
                CLIRenderer.messaggio(riga);
            }
            CLIRenderer.messaggio("Ultimo aggiornamento: "
                    + progress.getUpdatedAt().format(DT_FMT));
        }
    }

    public void mostraMenuStudente() {
        CLIRenderer.sezione("Azioni");
        CLIRenderer.voceMenu(1, "Annota progressi");
        CLIRenderer.voceMenu(2, "Assegna attività");
        CLIRenderer.voceMenu(3, "Visualizza attività assegnate");
        CLIRenderer.voceMenu(4, "Elimina attività");
        CLIRenderer.voceMenu(5, "Visualizza storico lezioni");
        CLIRenderer.voceMenuZero("Torna alla lista");
    }

    public void mostraStoricoLezioni(List<BookingResponseBean> completed) {
        CLIRenderer.sezione("Storico lezioni effettuate");
        if (completed.isEmpty()) {
            CLIRenderer.messaggio("Nessuna lezione ancora effettuata.");
            return;
        }
        for (BookingResponseBean b : completed) {
            System.out.printf("  %s  %s  %s  %s – %s%n",
                    CLIRenderer.BULLET,
                    b.getTimeSlot().getDate(),
                    b.getSubject().getName(),
                    b.getTimeSlot().getStartTime(),
                    b.getTimeSlot().getEndTime());
        }
    }

    public void mostraAttivitaPerEliminazione(List<ActivityBean> activities) {
        CLIRenderer.sezione("Seleziona attività da eliminare");
        for (int i = 0; i < activities.size(); i++) {
            ActivityBean a = activities.get(i);
            String simbolo = a.isCompleted() ? CLIRenderer.DONE : CLIRenderer.PENDING;
            System.out.printf("  [%d] %s  %s%n", i + 1, simbolo, a.getDescription());
        }
        CLIRenderer.voceMenuZero("Annulla");
    }

    public void mostraAttivita(List<ActivityBean> activities) {
        CLIRenderer.sezione("Attività assegnate");
        if (activities.isEmpty()) {
            CLIRenderer.messaggio("Nessuna attività assegnata.");
        } else {
            for (ActivityBean a : activities) {
                String simbolo = a.isCompleted() ? CLIRenderer.DONE : CLIRenderer.PENDING;
                String stato   = a.isCompleted() ? "Completata" : "In sospeso";
                System.out.printf("  %s  %-10s  %s  (%s)%n",
                        simbolo, stato, a.getDescription(),
                        a.getCreatedAt().toLocalDate());
            }
        }
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

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public String chiediTesto(String prompt) {
        return CLIRenderer.chiediCampo(prompt);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo("[ INVIO per tornare ]");
    }
}