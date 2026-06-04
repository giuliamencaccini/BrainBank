package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.ManageStudentsView;

import java.util.List;

public class ManageStudentsCLI extends AbstractCLIState {

    private final ActivityController activityController = new ActivityController();
    private final ManageStudentsView view = new ManageStudentsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<StudentBean> students = activityController.getStudents();
            view.mostraStudenti(students);

            if (students.isEmpty()) { goBack(context); return; }

            int choice = view.chiediScelta("Seleziona uno studente", 0, students.size());
            if (choice == 0) { goBack(context); return; }

            manageStudent(context, students.get(choice - 1));

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
            goBack(context);
        }
    }

    private void manageStudent(CLIStateMachine context, StudentBean student) throws DAOException {
        while (true) {
            List<BookingResponseBean> upcoming = activityController.getUpcomingLessons(student.getId());
            ProgressBean progress = activityController.getProgress(student.getId());

            view.mostraSchedaStudente(student, upcoming, progress);
            view.mostraMenuStudente();

            int choice = view.chiediScelta("Scelta", 0, 5);
            switch (choice) {
                case 1 -> annotaProgressi(student);
                case 2 -> assegnaAttivita(student);
                case 3 -> visualizzaAttivita(student);
                case 4 -> eliminaAttivita(student);
                case 5 -> visualizzaStorico(student);
                case 0 -> { goBack(context); return; }
                default -> view.mostraMessaggio("❌ Scelta non valida.");
            }
        }
    }

    private void annotaProgressi(StudentBean student) throws DAOException {
        ProgressBean existing = activityController.getProgress(student.getId());
        if (existing != null) {
            view.mostraMessaggio("Progressi attuali: " + existing.getNotes());
            view.mostraMessaggio("Riscrivi il testo modificato:");
        }
        String notes = view.chiediTesto("Note");
        if (notes.isBlank()) { view.mostraMessaggio("Note non valide."); return; }
        activityController.updateProgress(new ProgressBean(student, notes, null));
        view.mostraSuccesso("Progressi aggiornati.");
    }

    private void assegnaAttivita(StudentBean student) throws DAOException {
        String description = view.chiediTesto("Descrizione attività");
        if (description.isBlank()) { view.mostraMessaggio("Descrizione non valida."); return; }
        ActivityBean bean = new ActivityBean(0, student, description, false, null);
        activityController.assignActivity(bean);
        view.mostraSuccesso("Attività assegnata.");
    }

    private void visualizzaAttivita(StudentBean student) throws DAOException {
        view.mostraAttivita(activityController.getActivities(student.getId()));
        view.attesaInvio();
    }

    private void visualizzaStorico(StudentBean student) throws DAOException {
        view.mostraStoricoLezioni(activityController.getCompletedLessons(student.getId()));
        view.attesaInvio();
    }

    private void eliminaAttivita(StudentBean student) throws DAOException {
        List<ActivityBean> activities = activityController.getActivities(student.getId());
        if (activities.isEmpty()) { view.mostraMessaggio("Nessuna attività da eliminare."); return; }
        view.mostraAttivitaPerEliminazione(activities);
        int choice = view.chiediScelta("Seleziona attività da eliminare", 0, activities.size());
        if (choice == 0) return;
        if (!view.chiediConferma("Sei sicuro? L'operazione è irreversibile.")) {
            view.mostraMessaggio("Operazione annullata."); return;
        }
        activityController.deleteActivity(activities.get(choice - 1).getId());
        view.mostraSuccesso("Attività eliminata.");
    }
}