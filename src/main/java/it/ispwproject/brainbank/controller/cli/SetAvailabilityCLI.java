package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.SetAvailabilityView;

import java.time.LocalDate;
import java.time.LocalTime;

public class SetAvailabilityCLI {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final SetAvailabilityView view = new SetAvailabilityView();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            LocalDate date      = view.chiediData();
            LocalTime startTime = view.chiediOra("Ora inizio");
            LocalTime endTime   = view.chiediOra("Ora fine");

            TimeSlotBean slotBean = new TimeSlotBean(0, date, startTime, endTime, true);

            view.mostraMessaggio("  Data     : " + date);
            view.mostraMessaggio("  Orario   : " + startTime + " – " + endTime);

            if (!view.chiediConferma("Confermare lo slot?")) {
                view.mostraMessaggio("Operazione annullata.");
                return CLIState.DASHBOARD_TUTOR;
            }

            availabilityController.addSlot(slotBean);
            view.mostraSuccesso();

        } catch (DAOException | AvailabilityException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_TUTOR;
    }
}