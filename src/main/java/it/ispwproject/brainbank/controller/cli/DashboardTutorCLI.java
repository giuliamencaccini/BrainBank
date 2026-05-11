package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.util.Scanner;

public class DashboardTutorCLI {

    private static final String SEPARATOR = "─".repeat(50);
    private final Scanner scanner = new Scanner(System.in);

    public CLIState start() {
        User user = SessionManager.getInstance().getLoggedUser();

        System.out.println();
        System.out.println(SEPARATOR);
        System.out.printf("  Bentornato %s!  –  Tutor%n", user.getName());
        System.out.println(SEPARATOR);
        System.out.println("  [1] Disponibilità");
        System.out.println("  [2] I miei slot");
        System.out.println("  [3] Gestisci studenti");
        System.out.println("  [0] Logout");

        return switch (readChoice()) {
            case "1" -> CLIState.SET_AVAILABILITY;
            case "2" -> CLIState.VIEW_SLOTS;
            case "3" -> CLIState.MANAGE_STUDENTS;
            case "0" -> onLogout();
            default  -> {
                System.out.println("  ❌ Scelta non valida.");
                yield CLIState.DASHBOARD_TUTOR;
            }
        };
    }

    private CLIState onLogout() {
        SessionManager.getInstance().clearSession();
        System.out.println("  ✓ Logout effettuato.");
        return CLIState.INIZIALE;
    }

    private String readChoice() {
        System.out.printf("%n  Scelta: ");
        return scanner.nextLine().trim();
    }
}
