package it.ispwproject;

import it.ispwproject.brainbank.controller.cli.MainCLI;
import it.ispwproject.brainbank.controller.cli.ModeSelectorCLI;

public class Main {

    public static void main(String[] args) {
        ModeSelectorCLI modeSelector = new ModeSelectorCLI();
        boolean proceed = modeSelector.start();
        if (proceed) {
            MainCLI.start();
        }
    }
}