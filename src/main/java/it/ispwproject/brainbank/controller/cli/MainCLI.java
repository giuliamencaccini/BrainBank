package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.CLIStateMachine;
import it.ispwproject.brainbank.pattern.state.CLIStateMachineImpl;

public class MainCLI {

    public static void start() {
        CLIStateMachine machine = new CLIStateMachineImpl();
        machine.start();
    }
}