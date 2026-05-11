package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.view.MonitorProgressView;

public class MonitorProgressCLI {

    private final MonitorProgressView view = new MonitorProgressView();

    public CLIState start() {
        view.mostraMessaggio("Funzionalità non ancora implementata.");
        return CLIState.DASHBOARD_TUTOR;
    }
}