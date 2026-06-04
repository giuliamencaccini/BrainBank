package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.controller.applicativo.RegistrationController;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.RegistrationException;
import it.ispwproject.brainbank.view.cli.RegistrationView;

import java.util.List;

public class RegistrationCLI extends AbstractCLIState {

    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationView view = new RegistrationView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
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
                bean.setSubjects(view.chiediMaterie(allSubjects));
            }

            registrationController.register(bean);
            view.mostraSuccesso();
            goNext(context, new LoginCLI());

        } catch (RegistrationException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);
        } catch (DAOException e) {
            view.mostraErrore("Errore di sistema: " + e.getMessage());
            goNext(context, new LoginCLI());
        }
    }
}