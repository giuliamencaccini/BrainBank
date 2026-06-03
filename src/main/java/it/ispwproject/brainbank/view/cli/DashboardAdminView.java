package it.ispwproject.brainbank.view.cli;

public class DashboardAdminView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Admin");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Statistiche e report");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}