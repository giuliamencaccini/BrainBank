package it.ispwproject.brainbank.controller.cli;

public class MainCLI {

    public static void start() {
        CLIState stato = CLIState.INIZIALE;

        while (stato != CLIState.USCITA) {
            stato = switch (stato) {

                // ── Comuni ───────────────────────────────────────────────
                case INIZIALE      -> new InitialCLI().start();
                case LOGIN         -> new LoginCLI().start();
                case REGISTRAZIONE -> new RegistrationCLI().start();

                // ── Student ──────────────────────────────────────────────
                case DASHBOARD_STUDENT -> new DashboardStudentCLI().start();
                case BOOK_LESSON       -> new BookLessonCLI().start();
                case VIEW_BOOKINGS     -> new ViewBookingsCLI().start();
                case CANCEL_BOOKING    -> new CancelBookingCLI().start();
                case VIEW_TODO         -> new ViewToDoCLI().start();

                // ── Tutor ────────────────────────────────────────────────
                case DASHBOARD_TUTOR  -> new DashboardTutorCLI().start();
                case SET_AVAILABILITY -> new SetAvailabilityCLI().start();
                case VIEW_SLOTS       -> new ViewSlotsCLI().start();
                case MANAGE_STUDENTS  -> new ManageStudentsCLI().start();
                case ASSIGN_ACTIVITY  -> new AssignActivityCLI().start();
                case MONITOR_PROGRESS -> new MonitorProgressCLI().start();

                default -> CLIState.USCITA;
            };
        }

        System.out.println("\n  Arrivederci!");
    }
}