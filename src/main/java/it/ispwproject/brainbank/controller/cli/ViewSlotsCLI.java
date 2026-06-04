package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.controller.applicativo.AvailabilityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.ViewSlotsView;

import java.util.List;
import java.util.Map;

public class ViewSlotsCLI extends AbstractCLIState {

    private final AvailabilityController availabilityController = new AvailabilityController();
    private final ViewSlotsView view = new ViewSlotsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            Map<Integer, String> subjectBySlot = availabilityController.getSubjectBySlot();
            List<TimeSlotBean> futuri  = availabilityController.getSlots();
            List<TimeSlotBean> passati = availabilityController.getPastSlots();

            view.mostraSlots(futuri, subjectBySlot);
            view.mostraPassati(passati);

            List<TimeSlotBean> disponibili = futuri.stream()
                    .filter(TimeSlotBean::isAvailable).toList();

            if (!disponibili.isEmpty() && view.chiediEliminazioneSlot()) {
                view.mostraSlotDisponibili(disponibili);
                int choice = view.chiediScelta("Seleziona slot da eliminare", 0, disponibili.size());
                if (choice != 0) {
                    if (view.chiediConferma("Sei sicuro di voler eliminare questo slot?")) {
                        availabilityController.deleteSlot(disponibili.get(choice - 1).getId());
                        view.mostraSuccessoEliminazione();
                    } else {
                        view.mostraMessaggio("Operazione annullata.");
                    }
                }
            }
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}