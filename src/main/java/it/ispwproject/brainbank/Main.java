package it.ispwproject.brainbank;

import it.ispwproject.brainbank.controller.cli.MainCLI;
import it.ispwproject.brainbank.controller.cli.ModeSelectorCLI;
import it.ispwproject.brainbank.controller.gui.MainGUI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ── Step 1 — selezione modalità persistenza (sempre CLI) ─────
        ModeSelectorCLI modeSelector = new ModeSelectorCLI();
        boolean proceed = modeSelector.start();
        if (!proceed) return;

        // ── Step 2 — selezione interfaccia ───────────────────────────
        Scanner scanner = new Scanner(System.in);
        String scelta = "";

        while (!scelta.equals("1") && !scelta.equals("2")) {
            System.out.println("\n  ── Seleziona interfaccia");
            System.out.println("  [1] CLI  — interfaccia testuale");
            System.out.println("  [2] GUI  — interfaccia grafica");
            System.out.print("\n  Scelta: ");
            scelta = scanner.nextLine().trim();
            if (!scelta.equals("1") && !scelta.equals("2")) {
                System.out.println("  ❌ Scelta non valida.");
            }
        }

        if (scelta.equals("2")) {
            MainGUI.launch(args);
        } else {
            MainCLI.start();
        }
    }
}