package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.controller.applicativo.UserController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import it.ispwproject.brainbank.view.EditProfileView;

public class EditProfileCLI {

    private final UserController userController = new UserController();
    private final EditProfileView view = new EditProfileView();

    public CLIState start() {
        view.mostraIntestazione();
        view.mostraMenu();

        CLIState dashboard = SessionManager.getInstance().isStudent()
                ? CLIState.DASHBOARD_STUDENT
                : CLIState.DASHBOARD_TUTOR;

        return switch (view.chiediScelta()) {
            case "1" -> editEmail(dashboard);
            case "0" -> dashboard;
            default  -> {
                view.mostraErrore("Scelta non valida.");
                yield CLIState.EDIT_PROFILE;
            }
        };
    }

    private CLIState editEmail(CLIState dashboard) {
        String newEmail = view.chiediCampo("Nuova email");
        if (!view.chiediConferma("Confermare il cambio email a " + newEmail + "?")) {
            view.mostraMessaggio("Operazione annullata.");
            return CLIState.EDIT_PROFILE;
        }
        try {
            userController.updateEmail(newEmail);
            view.mostraSuccesso("Email aggiornata con successo.");
            return dashboard;
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
            return CLIState.EDIT_PROFILE;
        }
    }

    private CLIState editEmail() {
        String newEmail = view.chiediCampo("Nuova email");
        try {
            userController.updateEmail(newEmail);
            view.mostraSuccesso("Email aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        return CLIState.EDIT_PROFILE;
    }
}