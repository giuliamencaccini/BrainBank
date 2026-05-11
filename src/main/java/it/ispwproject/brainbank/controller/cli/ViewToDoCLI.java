package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.ActivityBean;
import it.ispwproject.brainbank.controller.applicativo.ActivityController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.ViewToDoView;


import java.util.List;

public class ViewToDoCLI {

    private final ActivityController activityController = new ActivityController();
    private final ViewToDoView view = new ViewToDoView();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            List<ActivityBean> activities = activityController.getMyActivities();
            view.mostraAttivita(activities);

            // Filtra solo quelle non completate
            List<ActivityBean> pending = activities.stream()
                    .filter(a -> !a.isCompleted())
                    .toList();

            if (pending.isEmpty()) {
                view.mostraMessaggio("Tutte le attività sono state completate!");
                return CLIState.DASHBOARD_STUDENT;
            }

            // Mostra solo le attività non completate per la selezione
            System.out.println("\n  ── Segna come completata");
            for (int i = 0; i < pending.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, pending.get(i).getDescription());
            }

            int choice = view.chiediScelta("Seleziona attività completata (0 = torna indietro)",
                    0, pending.size());
            if (choice == 0) return CLIState.DASHBOARD_STUDENT;

            ActivityBean selected = pending.get(choice - 1);
            activityController.markActivityCompleted(selected.getId());
            view.mostraSuccesso("Attività segnata come completata.");

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        return CLIState.DASHBOARD_STUDENT;
    }
}
