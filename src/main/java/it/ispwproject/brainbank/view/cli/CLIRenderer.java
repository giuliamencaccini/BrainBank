package it.ispwproject.brainbank.view.cli;

import java.util.Scanner;

/**
 * Utility di rendering condivisa per tutte le view CLI di BrainBank.
 * Centralizza:
 *  - costanti visive (larghezze, simboli, stili)
 *  - metodi di stampa (intestazioni, separatori, messaggi)
 *  - metodi di input interattivi (chiediScelta, chiediConferma, chiediCampo)
 * Nessuna logica di business: le view rimangono pure boundary.
 */
public final class CLIRenderer {


    //  Costanti visive
    public static final int    WIDTH     = 60;
    public static final String LINE      = "─".repeat(WIDTH);
    public static final String LINE_THIN = "╌".repeat(WIDTH);
    public static final String LINE_DECO = "═".repeat(WIDTH);

    // Simboli di stato normalizzati
    public static final String OK      = "✓";
    public static final String ERR     = "✗";
    public static final String BULLET  = "•";
    public static final String STAR    = "★";
    public static final String CLOCK   = "📅";
    public static final String AVAIL   = "○";   // slot disponibile
    public static final String BOOKED  = "●";   // slot prenotato
    public static final String PENDING = "◌";   // attività in sospeso
    public static final String DONE    = "◉";   // attività completata

    /** Scanner condiviso nell'intera applicazione CLI. */
    public static final Scanner SCANNER = new Scanner(System.in);

    private CLIRenderer() {}   // non istanziabile


    //  Intestazioni e separatori
    /**
     * Intestazione principale di schermata con bordo doppio e titolo centrato.
     * ════════════════════════════════════════════════════════════
     *   BrainBank – Titolo della schermata
     * ════════════════════════════════════════════════════════════
     */
    public static void intestazione(String titolo) {
        System.out.println();
        System.out.println(LINE_DECO);
        System.out.println("  " + titolo);
        System.out.println(LINE_DECO);
    }

    /**
     * Intestazione di benvenuto con ruolo evidenziato.
     * ════════════════════════════════════════════════════════════
     *   Bentornato, Mario!                           [ Studente ]
     * ════════════════════════════════════════════════════════════
     */
    public static void intestazioneBenvenuto(String nome, String ruolo) {
        String left  = "  Bentornato, " + nome + "!";
        String right = "[ " + ruolo + " ]";
        int spaces   = WIDTH - left.length() - right.length();
        String pad   = spaces > 0 ? " ".repeat(spaces) : "  ";
        System.out.println();
        System.out.println(LINE_DECO);
        System.out.println(left + pad + right);
        System.out.println(LINE_DECO);
    }

    //Separatore di sezione con etichetta (Titolo sezione)
    public static void sezione(String etichetta) {
        System.out.println("\n  ── " + etichetta);
    }

    //Separatore orizzontale semplice.
    public static void separatore() {
        System.out.println(LINE);
    }

    // Riga vuota
    public static void vuota() {
        System.out.println();
    }


    //  Messaggi
    public static void messaggio(String testo) {
        System.out.println("  " + testo);
    }

    public static void successo(String testo) {
        System.out.println("  " + OK + " " + testo);
        System.out.println(LINE);
    }

    public static void errore(String testo) {
        System.out.println("  " + ERR + " " + testo);
    }

    //Stampa una riga con etichetta e valore allineati
    public static void campo(String etichetta, String valore) {
        System.out.printf("  %-10s: %s%n", etichetta, valore != null ? valore : "—");
    }

    //Stampa una riga numerata di un menu
    public static void voceMenu(int numero, String etichetta) {
        System.out.printf("  [%d] %s%n", numero, etichetta);
    }

    //Stampa la voce 0 (sempre "indietro" o "logout")
    public static void voceMenuZero(String etichetta) {
        System.out.println();
        System.out.printf("  [0] %s%n", etichetta);
    }


    //  Input interattivi

    /**
     * Chiede all'utente di selezionare un numero intero nell'intervallo [min, max].
     * Ripete finché l'input non è valido.
     */
    public static int chiediScelta(String prompt, int min, int max) {
        while (true) {
            System.out.printf("%n  %s [%d–%d]: ", prompt, min, max);
            String input = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.printf("  Inserisci un numero tra %d e %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Input non valido.");
            }
        }
    }

    /**
     * Variante stringa (per menu che restituisce String)
     */
    public static String chiediSceltaStringa(String prompt) {
        System.out.printf("\n  %s: ", prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * Chiede conferma s/n. Ripete finché l'input non è valido.
     */
    public static boolean chiediConferma(String prompt) {
        while (true) {
            System.out.printf("  %s [s/n]: ", prompt);
            String input = SCANNER.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sì")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("  Rispondi con 's' oppure 'n'.");
        }
    }

    /**
     * Chiede un campo di testo con label.
     */
    public static String chiediCampo(String label) {
        System.out.printf("  %s: ", label);
        return SCANNER.nextLine().trim();
    }

    /**
     * Centra una stringa nella larghezza WIDTH con padding di spazi.
     */
    public static String centra(String testo) {
        int pad = Math.max(0, (WIDTH - testo.length()) / 2);
        return " ".repeat(pad) + testo;
    }
}
