package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;
import it.ispwproject.brainbank.view.cli.CancelBookingView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class CancelBookingCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final CancelBookingView view = new CancelBookingView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int studentId = SessionManager.getInstance().getLoggedUser().getId();
        try {
            List<BookingResponseBean> cancellable = bookingController
                    .getStudentBookings(studentId)
                    .stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getTimeSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())) ||
                            (b.getTimeSlot().getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) &&
                                    b.getTimeSlot().getEndTime().isAfter(LocalTime.now(ZoneId.systemDefault()))))
                    .toList();

            if (cancellable.isEmpty()) {
                view.mostraMessaggio("Nessuna prenotazione attiva da annullare.");
                goBack(context);
                return;
            }

            view.mostraPrenotazioniAnnullabili(cancellable);
            int choice = view.chiediScelta("Seleziona la prenotazione da annullare", 0, cancellable.size());
            if (choice == 0) { goBack(context); return; }

            BookingResponseBean selected = cancellable.get(choice - 1);
            view.mostraRiepilogo(selected);

            if (!view.chiediConferma("Sei sicuro di voler annullare?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            bookingController.cancelBooking(selected.getId(), studentId);
            view.mostraSuccesso();

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}