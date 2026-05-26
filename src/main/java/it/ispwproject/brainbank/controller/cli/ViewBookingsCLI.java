package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import it.ispwproject.brainbank.view.ViewBookingsView;

public class ViewBookingsCLI {

    private final BookingController bookingController = new BookingController();
    private final ViewBookingsView view = new ViewBookingsView();
    private final int studentId = SessionManager.getInstance().getLoggedUser().getId();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            view.mostraPrenotazioni(bookingController.getStudentBookings(studentId));
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_STUDENT;
    }
}