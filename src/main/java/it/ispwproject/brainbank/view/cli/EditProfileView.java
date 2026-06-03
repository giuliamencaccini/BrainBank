package it.ispwproject.brainbank.view.cli;

public class EditProfileView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("BrainBank  –  Profilo");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Modifica email");
        CLIRenderer.voceMenuZero("Indietro");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}

