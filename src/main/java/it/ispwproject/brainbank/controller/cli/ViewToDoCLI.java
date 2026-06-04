package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.ActivityBean;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.ViewToDoView;

import java.util.List;

public class ViewToDoCLI extends AbstractCLIState {

    private final ActivityController activityController = new ActivityController();
    private final ViewToDoView view = new ViewToDoView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<ActivityBean> activities = activityController.getMyActivities();
            view.mostraAttivita(activities);

            List<ActivityBean> pending = activities.stream()
                    .filter(a -> !a.isCompleted()).toList();

            if (pending.isEmpty()) {
                view.mostraMessaggio("Tutte le attività sono state completate!");
                goBack(context);
                return;
            }

            if (!view.chiediConferma("Vuoi segnare un'attività come completata?")) {
                goBack(context);
                return;
            }

            view.mostraPendingPerSelezione(pending);
            int choice = view.chiediScelta("Seleziona attività", 0, pending.size());
            if (choice == 0) { goBack(context); return; }

            activityController.markActivityCompleted(pending.get(choice - 1).getId());
            view.mostraSuccesso("Attività segnata come completata.");

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}