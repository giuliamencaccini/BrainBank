package it.ispwproject.brainbank.controller.cli;


import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;
import it.ispwproject.brainbank.view.cli.InitialView;

/**
 * Stato iniziale della CLIStateMachine.
 * Primo ConcreteState — punto di ingresso della CLI.
 */
public class InitialCLI extends AbstractCLIState {

    private final InitialView view = new InitialView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraBenvenuto();
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();
        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new LoginCLI());
            case "2" -> goNext(context, new RegistrationCLI());
            case "0" -> context.setState(null);
            default  -> {
                view.mostraErrore("Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}