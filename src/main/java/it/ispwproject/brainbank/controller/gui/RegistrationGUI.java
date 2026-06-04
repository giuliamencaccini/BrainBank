package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.controller.applicativo.RegistrationController;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.RegistrationException;
import it.ispwproject.brainbank.view.gui.RegistrationGUIView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;

public class RegistrationGUI {

    private final Stage stage;
    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationGUIView view = new RegistrationGUIView();

    public RegistrationGUI(Stage stage) { this.stage = stage; }

    public void show() {
        view.registerBtn.setOnAction(e -> handleRegistration());
        stage.setScene(GUIUtils.createScene(view.buildRoot(MainGUI::showLogin)));
        stage.show();
        loadSubjects();
    }

    private void loadSubjects() {
        try {
            List<SubjectBean> subjects = registrationController.getAvailableSubjects();
            view.setSubjects(subjects);
        } catch (DAOException e) {
            view.setError("Errore nel caricamento delle materie.");
        }
    }

    private void handleRegistration() {
        RegistrationBean bean = new RegistrationBean();
        bean.setName(view.nameField.getText().trim());
        bean.setSurname(view.surnameField.getText().trim());
        bean.setEmail(view.emailField.getText().trim());
        bean.setPassword(view.passwordField.getText().trim());
        bean.setConfirmPassword(view.confirmPasswordField.getText().trim());
        bean.setRole(view.tutorRadio.isSelected() ? Role.TUTOR : Role.STUDENT);

        if (view.tutorRadio.isSelected()) {
            bean.setBio(view.bioField.getText().trim());
            bean.setSubjects(view.getSelectedSubjects());
        }

        try {
            registrationController.register(bean);
            showSuccess();
        } catch (RegistrationException e) {
            view.setError(e.getMessage());
        } catch (DAOException e) {
            view.setError("Errore di sistema: " + e.getMessage());
        }
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registrazione completata");
        alert.setHeaderText(null);
        alert.setContentText("Registrazione completata! Ora puoi effettuare il login.");
        alert.showAndWait();
        MainGUI.showLogin();
    }
}   

