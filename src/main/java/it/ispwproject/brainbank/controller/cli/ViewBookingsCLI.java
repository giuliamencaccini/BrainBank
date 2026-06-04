package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.ViewBookingsView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class ViewBookingsCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final ViewBookingsView view = new ViewBookingsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int studentId = SessionManager.getInstance().getLoggedUser().getId();
        try {
            List<BookingResponseBean> all  = bookingController.getStudentBookings(studentId);
            List<BookingResponseBean> past = bookingController.getStudentPastBookings(studentId);

            List<BookingResponseBean> confirmed = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getTimeSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())) ||
                            (b.getTimeSlot().getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) &&
                                    b.getTimeSlot().getEndTime().isAfter(LocalTime.now(ZoneId.systemDefault()))))
                    .sorted((a, b) -> a.getTimeSlot().getDate().compareTo(b.getTimeSlot().getDate()))
                    .toList();

            List<BookingResponseBean> cancelled = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .sorted((a, b) -> a.getTimeSlot().getDate().compareTo(b.getTimeSlot().getDate()))
                    .toList();

            boolean running = true;
            while (running) {
                view.mostraTab(confirmed.size(), cancelled.size(), past.size());
                int scelta = view.chiediScelta("Scelta", 0, 3);
                switch (scelta) {
                    case 1 -> view.mostraConfermate(confirmed);
                    case 2 -> view.mostraCancellate(cancelled);
                    case 3 -> view.mostraScadute(past);
                    case 0 -> { running = false; }
                }
            }
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}