package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.ManageStudentsView;

import java.util.List;

public class ManageStudentsCLI {

    private final ActivityController activityController = new ActivityController();
    private final ManageStudentsView view = new ManageStudentsView();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            List<StudentBean> students = activityController.getStudents();
            view.mostraStudenti(students);

            if (students.isEmpty()) return CLIState.DASHBOARD_TUTOR;

            int choice = view.chiediScelta("Seleziona uno studente", 0, students.size());
            if (choice == 0) return CLIState.DASHBOARD_TUTOR;

            return manageStudent(students.get(choice - 1));

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
            return CLIState.DASHBOARD_TUTOR;
        }
    }

    private CLIState manageStudent(StudentBean student) throws DAOException {
        while (true) {
            List<BookingResponseBean> completed = activityController.getCompletedLessons(student.getId());
            List<BookingResponseBean> upcoming  = activityController.getUpcomingLessons(student.getId());
            ProgressBean progress = activityController.getProgress(student.getId());

            view.mostraSchedaStudente(student, completed, upcoming, progress);
            view.mostraMenuStudente();

            int choice = view.chiediScelta("Scelta", 0, 4);

            switch (choice) {
                case 1 -> annotaProgressi(student);
                case 2 -> assegnaAttivita(student);
                case 3 -> visualizzaAttivita(student);
                case 4 -> eliminaAttivita(student);
                case 0 -> { return CLIState.DASHBOARD_TUTOR; }
                default -> view.mostraMessaggio("❌ Scelta non valida.");
            }
        }
    }

    private void annotaProgressi(StudentBean student) throws DAOException {
        String notes = view.chiediTesto("Inserisci note sui progressi");
        if (notes.isBlank()) {
            view.mostraMessaggio("Note non valide.");
            return;
        }
        activityController.updateProgress(new ProgressBean(student, notes, null));
        view.mostraSuccesso("Progressi aggiornati.");
    }

    private void assegnaAttivita(StudentBean student) throws DAOException {
        String description = view.chiediTesto("Descrizione attività da assegnare");
        if (description.isBlank()) {
            view.mostraMessaggio("Descrizione non valida.");
            return;
        }
        ActivityBean bean = new ActivityBean(0, student, description, false, null);
        activityController.assignActivity(bean);
        view.mostraSuccesso("Attività assegnata.");
    }

    private void visualizzaAttivita(StudentBean student) throws DAOException {
        List<ActivityBean> activities = activityController.getActivities(student.getId());
        view.mostraAttivita(activities);
    }

    private void eliminaAttivita(StudentBean student) throws DAOException {
        List<ActivityBean> activities = activityController.getActivities(student.getId());
        if (activities.isEmpty()) {
            view.mostraMessaggio("Nessuna attività da eliminare.");
            return;
        }
        view.mostraAttivitaPerEliminazione(activities);
        int choice = view.chiediScelta("Seleziona attività da eliminare", 0, activities.size());
        if (choice == 0) return;
        activityController.deleteActivity(activities.get(choice - 1).getId());
        view.mostraSuccesso("Attività eliminata.");
    }
}