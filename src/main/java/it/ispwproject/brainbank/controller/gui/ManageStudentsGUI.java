package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.gui.ManageStudentsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ManageStudentsGUI {

    private final Stage  stage;
    private final ActivityController  activityController = new ActivityController();
    private final ManageStudentsGUIView  view  = new ManageStudentsGUIView();

    public ManageStudentsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardTutor);

        try {
            view.studentCombo.getItems().setAll(activityController.getStudents());
        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }

        view.studentCombo.setOnAction(e -> {
            StudentBean selected = view.studentCombo.getValue();
            if (selected == null) return;
            loadStudentCard(selected);
        });

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }


    // Caricamento card studente
    private void loadStudentCard(StudentBean student) {
        VBox card = view.getStudentCard();
        try {
            ProgressBean             progress   = activityController.getProgress(student.getId());
            List<ActivityBean>       activities = activityController.getActivities(student.getId());
            List<BookingResponseBean> upcoming  = activityController.getUpcomingLessons(student.getId());
            List<BookingResponseBean> completed = activityController.getCompletedLessons(student.getId());

            view.buildStudentCard(card, student, progress, activities, upcoming, completed,
                    notes  -> handleUpdateProgress(student, notes, card),
                    desc   -> handleAssignActivity(student, desc, card),
                    activity -> handleDeleteActivity(activity, student, card));

        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }
    }


    // Azioni
    private void handleUpdateProgress(StudentBean student, String notes, VBox card) {
        if (notes.isBlank()) { view.errorLabel.setText("Le note non possono essere vuote."); return; }
        try {
            activityController.updateProgress(new ProgressBean(student, notes, null));
            showInfo("Progressi aggiornati con successo.");
            loadStudentCard(student);
        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    private void handleAssignActivity(StudentBean student, String desc, VBox card) {
        try {
            activityController.assignActivity(new ActivityBean(0, student, desc, false, null));
            loadStudentCard(student);
        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    private void handleDeleteActivity(ActivityBean activity, StudentBean student, VBox card) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText(null);
        confirm.setContentText("Sei sicuro di voler eliminare questa attività?\n\"" +
                activity.getDescription() + "\"\n\nL'operazione è irreversibile.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == javafx.scene.control.ButtonType.OK) {
                try {
                    activityController.deleteActivity(activity.getId());
                    loadStudentCard(student);
                } catch (DAOException ex) {
                    view.errorLabel.setText("Errore: " + ex.getMessage());
                }
            }
        });
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione completata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}