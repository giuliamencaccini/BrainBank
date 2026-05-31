package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.CancelBookingView;

import java.util.List;

public class CancelBookingCLI {

    private final BookingController bookingController = new BookingController();
    private final CancelBookingView view = new CancelBookingView();

    public CLIState start() {
        int studentId = SessionManager.getInstance().getLoggedUser().getId();
        view.mostraIntestazione();

        try {
            List<BookingResponseBean> cancellable = bookingController
                    .getStudentBookings(studentId)
                    .stream()
                    .filter(b -> !b.getStatus().equals(BookingStatus.CANCELLED.name()))
                    .toList();

            if (cancellable.isEmpty()) {
                view.mostraMessaggio("Nessuna prenotazione attiva da annullare.");
                return CLIState.DASHBOARD_STUDENT;
            }

            view.mostraPrenotazioniAnnullabili(cancellable);

            int choice = view.chiediScelta("Seleziona la prenotazione da annullare", 0, cancellable.size());
            if (choice == 0) return CLIState.DASHBOARD_STUDENT;

            BookingResponseBean selected = cancellable.get(choice - 1);
            view.mostraRiepilogo(selected);

            if (!view.chiediConferma("Sei sicuro di voler annullare?")) {
                view.mostraMessaggio("Operazione annullata.");
                return CLIState.DASHBOARD_STUDENT;
            }

            bookingController.cancelBooking(selected.getId(), studentId);
            view.mostraSuccesso();

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_STUDENT;
    }
}