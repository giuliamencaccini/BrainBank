package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.controller.applicativo.RegistrationController;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.RegistrationException;
import it.ispwproject.brainbank.view.cli.RegistrationView;

import java.util.List;

public class RegistrationCLI {

    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationView view = new RegistrationView();

    public CLIState start() {
        view.mostraIntestazione();

        try {
            RegistrationBean bean = new RegistrationBean();

            bean.setName(view.chiediCampo("Nome"));
            bean.setSurname(view.chiediCampo("Cognome"));
            bean.setEmail(view.chiediCampo("Email"));
            bean.setPassword(view.chiediPassword("Password"));
            bean.setConfirmPassword(view.chiediPassword("Conferma password"));

            Role role = view.chiediRuolo();
            bean.setRole(role);

            if (role == Role.TUTOR) {
                bean.setBio(view.chiediCampo("Bio (breve descrizione)"));

                List<SubjectBean> allSubjects = registrationController.getAvailableSubjects();
                List<SubjectBean> selected = view.chiediMaterie(allSubjects);
                bean.setSubjects(selected);
            }

            registrationController.register(bean);
            view.mostraSuccesso();

        } catch (RegistrationException e) {
            view.mostraErrore(e.getMessage());
            return CLIState.REGISTRAZIONE;
        } catch (DAOException e) {
            view.mostraErrore("Errore di sistema: " + e.getMessage());
        }

        return CLIState.LOGIN;
    }
}