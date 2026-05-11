package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.util.singleton.SessionManager;
import it.ispwproject.brainbank.view.DashboardStudentView;

public class DashboardStudentCLI {

    private final DashboardStudentView view = new DashboardStudentView();

    public CLIState start() {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
        view.mostraMenu();

        return switch (view.chiediScelta()) {
            case "1" -> CLIState.BOOK_LESSON;
            case "2" -> CLIState.VIEW_BOOKINGS;
            case "3" -> CLIState.CANCEL_BOOKING;
            case "4" -> CLIState.VIEW_TODO;
            case "0" -> onLogout();
            default  -> {
                view.mostraMessaggio("❌ Scelta non valida.");
                yield CLIState.DASHBOARD_STUDENT;
            }
        };
    }

    private CLIState onLogout() {
        SessionManager.getInstance().clearSession();
        view.mostraMessaggio("✓ Logout effettuato.");
        return CLIState.INIZIALE;
    }
}