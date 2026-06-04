package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.SetAvailabilityView;

import java.time.LocalDate;
import java.time.LocalTime;

public class SetAvailabilityCLI extends AbstractCLIState {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final SetAvailabilityView view = new SetAvailabilityView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            LocalDate date      = view.chiediData();
            LocalTime startTime = view.chiediOra("Ora inizio");
            LocalTime endTime   = view.chiediOra("Ora fine");

            TimeSlotBean slotBean = new TimeSlotBean(0, date, startTime, endTime, true);
            view.mostraMessaggio("  Data     : " + date);
            view.mostraMessaggio("  Orario   : " + startTime + " – " + endTime);

            if (!view.chiediConferma("Confermare lo slot?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            availabilityController.addSlot(slotBean);
            view.mostraSuccesso();

        } catch (DAOException | AvailabilityException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}