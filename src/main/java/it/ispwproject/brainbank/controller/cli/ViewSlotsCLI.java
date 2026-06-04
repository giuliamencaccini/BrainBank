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

            List<TimeSlotBean> disponibili = futuri.stream().filter(TimeSlotBean::isAvailable).toList();
            List<TimeSlotBean> prenotati   = futuri.stream().filter(s -> !s.isAvailable()).toList();

            boolean running = true;
            while (running) {
                view.mostraTab(disponibili.size(), prenotati.size(), passati.size());
                int scelta = view.chiediScelta("Scelta", 0, 4);
                switch (scelta) {
                    case 1 -> view.mostraDisponibili(disponibili);
                    case 2 -> view.mostraPrenotati(prenotati, subjectBySlot);
                    case 3 -> view.mostraPassati(passati, subjectBySlot);
                    case 4 -> {
                        if (!disponibili.isEmpty()) {
                            view.mostraSlotDisponibili(disponibili);
                            int choice = view.chiediScelta("Seleziona slot da eliminare", 0, disponibili.size());
                            if (choice != 0) {
                                if (view.chiediConferma("Sei sicuro di voler eliminare questo slot?")) {
                                    availabilityController.deleteSlot(disponibili.get(choice - 1).getId());
                                    view.mostraSuccessoEliminazione();
                                    // ricarica dati aggiornati
                                    running = false;
                                } else {
                                    view.mostraMessaggio("Operazione annullata.");
                                }
                            }
                        } else {
                            view.mostraMessaggio("Nessuno slot disponibile da eliminare.");
                        }
                    }
                    case 0 -> running = false;
                }
            }
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}