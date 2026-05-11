package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.view.InitialView;

public class InitialCLI {

    private final InitialView view = new InitialView();

    public CLIState start() {
        view.mostraBenvenuto();
        view.mostraMenu();

        return switch (view.chiediScelta()) {
            case "1" -> CLIState.LOGIN;
            case "2" -> CLIState.REGISTRAZIONE;
            case "0" -> CLIState.USCITA;
            default  -> {
                view.mostraErrore("Scelta non valida.");
                yield CLIState.INIZIALE;
            }
        };
    }
}
