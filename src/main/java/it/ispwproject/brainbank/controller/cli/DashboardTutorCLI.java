package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.DashboardTutorView;

public class DashboardTutorCLI extends AbstractCLIState {

    private final DashboardTutorView view = new DashboardTutorView();

    @Override
    public void entry(CLIStateMachine context) {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();
        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new SetAvailabilityCLI());
            case "2" -> goNext(context, new ViewSlotsCLI());
            case "3" -> goNext(context, new ManageStudentsCLI());
            case "4" -> goNext(context, new EditProfileCLI());
            case "0" -> {
                try {
                    ConnectionFactory.clearRole();
                    SessionManager.getInstance().clearSession();
                    view.mostraMessaggio("✓ Logout effettuato.");
                    goNext(context, new InitialCLI());
                } catch (java.sql.SQLException ex) {
                    view.mostraMessaggio("❌ Errore: impossibile effettuare il logout in sicurezza. Riprova.");
                    goNext(context, this);
                }
            }
            default -> {
                view.mostraMessaggio("❌ Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}