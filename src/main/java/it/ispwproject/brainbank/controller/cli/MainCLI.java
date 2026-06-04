package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.CLIStateMachineImpl;

public class MainCLI {

    public static void start() {
        CLIStateMachineImpl machine = new CLIStateMachineImpl();
        machine.start();
    }
}