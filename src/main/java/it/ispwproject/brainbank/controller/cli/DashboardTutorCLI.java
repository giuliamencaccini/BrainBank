package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.DashboardTutorView;

public class DashboardTutorCLI {

    private final DashboardTutorView view = new DashboardTutorView();

    public CLIState start() {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
        view.mostraMenu();

        return switch (view.chiediScelta()) {
            case "1" -> CLIState.SET_AVAILABILITY;
            case "2" -> CLIState.VIEW_SLOTS;
            case "3" -> CLIState.MANAGE_STUDENTS;
            case "4" -> CLIState.EDIT_PROFILE;
            case "0" -> onLogout();
            default  -> {
                view.mostraMessaggio("❌ Scelta non valida.");
                yield CLIState.DASHBOARD_TUTOR;
            }
        };
    }

    private CLIState onLogout() {
        try { ConnectionFactory.clearRole(); } catch (java.sql.SQLException ex) { /* ignora */ }
        SessionManager.getInstance().clearSession();
        view.mostraMessaggio("✓ Logout effettuato.");
        return CLIState.INIZIALE;
    }
}