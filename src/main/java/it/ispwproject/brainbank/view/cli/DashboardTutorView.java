package it.ispwproject.brainbank.view.cli;

public class DashboardTutorView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Tutor");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Aggiungi disponibilità");
        CLIRenderer.voceMenu(2, "I miei slot");
        CLIRenderer.voceMenu(3, "Gestisci studenti");
        CLIRenderer.voceMenu(4, "Profilo");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}