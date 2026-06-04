package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.controller.applicativo.LoginController;
import it.ispwproject.brainbank.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.LoginView;

public class LoginCLI extends AbstractCLIState {

    private final LoginController loginController = new LoginController();
    private final LoginView view = new LoginView();

    @Override
    public void action(CLIStateMachine context) {
        String[] credenziali = view.chiediCredenziali();
        String email    = credenziali[0];
        String password = credenziali[1];

        if (email.isEmpty() || password.isEmpty()) {
            view.mostraErroreInput();
            goNext(context, this);
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);
            String nome = SessionManager.getInstance().getLoggedUser().getName();
            view.mostraSuccesso(nome);

            switch (result) {
                case SUCCESSO_STUDENT -> goNext(context, new DashboardStudentCLI());
                case SUCCESSO_TUTOR   -> goNext(context, new DashboardTutorCLI());
                case SUCCESSO_ADMIN   -> goNext(context, new DashboardAdminCLI());
            }
        } catch (LoginException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);
        }
    }
}