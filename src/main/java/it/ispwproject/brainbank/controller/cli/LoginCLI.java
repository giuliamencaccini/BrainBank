package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.controller.applicativo.LoginController;
import it.ispwproject.brainbank.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.LoginView;

public class LoginCLI {

    private final LoginController loginController = new LoginController();
    private final LoginView view = new LoginView();

    public CLIState start() {
        String[] credenziali = view.chiediCredenziali();
        String email    = credenziali[0];
        String password = credenziali[1];

        if (email.isEmpty() || password.isEmpty()) {
            view.mostraErroreInput();
            return CLIState.LOGIN;
        }

        try {
            LoginResult result = loginController.login(email, password);
            String nome = SessionManager.getInstance().getLoggedUser().getName();
            view.mostraSuccesso(nome);

            return switch (result) {
                case SUCCESSO_STUDENT -> CLIState.DASHBOARD_STUDENT;
                case SUCCESSO_TUTOR   -> CLIState.DASHBOARD_TUTOR;
                case SUCCESSO_ADMIN   -> CLIState.DASHBOARD_ADMIN;
            };

        } catch (LoginException e) {
            view.mostraErrore(e.getMessage());
            return CLIState.LOGIN;
        }
    }
}