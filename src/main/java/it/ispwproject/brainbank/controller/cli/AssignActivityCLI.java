package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.view.AssignActivityView;

public class AssignActivityCLI {

    private final AssignActivityView view = new AssignActivityView();

    public CLIState start() {
        view.mostraMessaggio("Funzionalità non ancora implementata.");
        return CLIState.DASHBOARD_TUTOR;
    }
}