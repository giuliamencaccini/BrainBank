package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.ViewSlotsView;

public class ViewSlotsCLI {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final ViewSlotsView view = new ViewSlotsView();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            view.mostraSlots(availabilityController.getSlots());
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_TUTOR;
    }
}