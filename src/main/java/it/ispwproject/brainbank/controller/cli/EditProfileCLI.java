package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.controller.applicativo.UserController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.EditProfileView;

public class EditProfileCLI extends AbstractCLIState {

    private final UserController userController = new UserController();
    private final EditProfileView view = new EditProfileView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
        var user = SessionManager.getInstance().getLoggedUser();
        view.mostraDatiAttuali(user.getName(), user.getSurname(), user.getEmail());
    }

    @Override
    public void action(CLIStateMachine context) {
        String scelta = "";

        while (!scelta.equals("0")) {
            view.mostraMenu();
            scelta = view.chiediScelta();

            switch (scelta) {
                case "1" -> editEmail();
                case "0" -> { /* esce dal while */ }
                default  -> view.mostraErrore("Scelta non valida.");
            }
        }

        goBack(context);
    }

    private void editEmail() {
        String newEmail = view.chiediCampo("Nuova email");
        if (!view.chiediConferma("Confermare il cambio email a " + newEmail + "?")) {
            view.mostraMessaggio("Operazione annullata.");
            return;
        }
        try {
            userController.updateEmail(newEmail);
            view.mostraSuccesso("Email aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
    }
}