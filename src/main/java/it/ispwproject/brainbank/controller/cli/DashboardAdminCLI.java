package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.util.singleton.SessionManager;
import it.ispwproject.brainbank.view.DashboardAdminView;

public class DashboardAdminCLI {

    private final DashboardAdminView view = new DashboardAdminView();

    public CLIState start() {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
        view.mostraMenu();

        return switch (view.chiediScelta()) {
            case "1" -> CLIState.REPORT_STATISTICS;
            case "0" -> onLogout();
            default  -> {
                view.mostraMessaggio("❌ Scelta non valida.");
                yield CLIState.DASHBOARD_ADMIN;
            }
        };
    }

    private CLIState onLogout() {
        SessionManager.getInstance().clearSession();
        view.mostraMessaggio("✓ Logout effettuato.");
        return CLIState.INIZIALE;
    }
}