package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import it.ispwproject.brainbank.view.ViewSlotsView;

import java.util.HashMap;
import java.util.Map;

public class ViewSlotsCLI {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final BookingController      bookingController      = new BookingController();
    private final ViewSlotsView view = new ViewSlotsView();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            int tutorId = SessionManager.getInstance().getLoggedUser().getId();

            // Mappa slotId → materia
            Map<Integer, String> subjectBySlot = new HashMap<>();
            try {
                for (BookingResponseBean b : bookingController.getTutorBookings(tutorId))
                    subjectBySlot.put(b.getTimeSlot().getId(), b.getSubject().getName());
            } catch (BookingException ignored) {}

            view.mostraSlots(availabilityController.getSlots(), subjectBySlot);

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_TUTOR;
    }
}