package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.controller.applicativo.LoginController;
import it.ispwproject.brainbank.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.view.gui.LoginGUIView;
import javafx.stage.Stage;

public class LoginGUI {

    private final Stage           stage;
    private final LoginController loginController = new LoginController();
    private final LoginGUIView       view            = new LoginGUIView();

    public LoginGUI(Stage stage) { this.stage = stage; }

    public void show() {
        stage.setScene(GUIUtils.createScene(
                view.buildRoot(this::handleLogin, MainGUI::showRegistration)));
        stage.show();
    }

    private void handleLogin() {
        String email    = view.emailField.getText().trim();
        String password = view.passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            view.setError("Inserisci sia email che password.");
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);
            switch (result) {
                case SUCCESSO_STUDENT -> MainGUI.showDashboardStudent();
                case SUCCESSO_TUTOR   -> MainGUI.showDashboardTutor();
                case SUCCESSO_ADMIN   -> MainGUI.showDashboardAdmin();
            }
        } catch (LoginException e) {
            view.setError(e.getMessage());
        }
    }
}