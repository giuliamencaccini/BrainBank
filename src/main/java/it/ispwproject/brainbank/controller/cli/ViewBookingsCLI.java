package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.ViewBookingsView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ViewBookingsCLI {

    private final BookingController bookingController = new BookingController();
    private final ViewBookingsView view = new ViewBookingsView();
    private final int studentId = SessionManager.getInstance().getLoggedUser().getId();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            List<BookingResponseBean> all  = bookingController.getStudentBookings(studentId);
            List<BookingResponseBean> past = bookingController.getStudentPastBookings(studentId);

            List<BookingResponseBean> confirmed = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getTimeSlot().getDate().isAfter(LocalDate.now()) ||
                            (b.getTimeSlot().getDate().isEqual(LocalDate.now()) &&
                                    b.getTimeSlot().getEndTime().isAfter(LocalTime.now())))
                    .sorted((a, b) -> a.getTimeSlot().getDate().compareTo(b.getTimeSlot().getDate()))
                    .toList();

            List<BookingResponseBean> cancelled = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .sorted((a, b) -> a.getTimeSlot().getDate().compareTo(b.getTimeSlot().getDate()))
                    .toList();

            while (true) {
                view.mostraTab(confirmed.size(), cancelled.size(), past.size());
                int scelta = view.chiediScelta("Scelta", 0, 3);

                switch (scelta) {
                    case 1 -> view.mostraConfermate(confirmed);
                    case 2 -> view.mostraCancellate(cancelled);
                    case 3 -> view.mostraScadute(past);
                    case 0 -> { return CLIState.DASHBOARD_STUDENT; }
                }
            }

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_STUDENT;
    }
}