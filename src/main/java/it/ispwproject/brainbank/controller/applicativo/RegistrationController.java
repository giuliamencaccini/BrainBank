package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.dao.RegistrationDAO;
import it.ispwproject.brainbank.dao.SubjectDAO;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.exception.RegistrationException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Subject;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.util.PasswordUtils;
import it.ispwproject.brainbank.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    private final RegistrationDAO registrationDAO;
    private final SubjectDAO subjectDAO;

    public RegistrationController() {
        this.registrationDAO = DAOFactory.getRegistrationDAO();
        this.subjectDAO = DAOFactory.getSubjectDAO();
    }

    public List<SubjectBean> getAvailableSubjects() throws DAOException {
        List<SubjectBean> result = new ArrayList<>();
        for (Subject subject : subjectDAO.getAll()) {
            result.add(new SubjectBean(subject.getId(), subject.getName()));
        }
        return result;
    }

    public void register(RegistrationBean bean)
            throws DAOException, RegistrationException {

        validateBean(bean);

        if (registrationDAO.emailExists(bean.getEmail())) {
            throw new RegistrationException("Email già registrata. Usa un'altra email.");
        }

        String hashedPassword;
        try {
            hashedPassword = PasswordUtils.hash(bean.getPassword());
        } catch (LoginException e) {
            throw new RegistrationException("Errore durante la codifica della password.", e);
        }

        User user;
        if (bean.getRole() == Role.TUTOR) {
            user = new Tutor(0, bean.getName(), bean.getSurname(),
                    bean.getEmail(), hashedPassword, bean.getBio());
        } else {
            user = new Student(0, bean.getName(), bean.getSurname(),
                    bean.getEmail(), hashedPassword);
        }

        List<Integer> subjectIds = new ArrayList<>();
        if (bean.getSubjects() != null) {
            for (SubjectBean subject : bean.getSubjects()) {
                subjectIds.add(subject.getId());
            }
        }

        registrationDAO.save(user, bean.getBio(), subjectIds);
    }

    private void validateBean(RegistrationBean bean) throws RegistrationException {
        if (bean == null) {
            throw new RegistrationException("Dati di registrazione non validi.");
        }
        validateRequiredField(bean.getName(), "Il nome è obbligatorio.");
        validateRequiredField(bean.getSurname(), "Il cognome è obbligatorio.");
        validateRequiredField(bean.getEmail(), "L'email è obbligatoria.");
        validateEmail(bean.getEmail());
        validatePassword(bean);
        validateRole(bean);
        validateTutorFields(bean);
    }

    private void validateRequiredField(String value, String message)
            throws RegistrationException {
        if (value == null || value.isBlank()) {
            throw new RegistrationException(message);
        }
    }

    private void validateEmail(String email) throws RegistrationException {
        if (!ValidationUtils.isValidEmail(email)) {
            throw new RegistrationException("Email non valida.");
        }
    }

    private void validatePassword(RegistrationBean bean) throws RegistrationException {
        if (bean.getPassword() == null || bean.getPassword().length() < 8) {
            throw new RegistrationException("La password deve essere di almeno 8 caratteri.");
        }
        if (!bean.getPassword().matches(".*[A-Z].*")) {
            throw new RegistrationException("La password deve contenere almeno una lettera maiuscola.");
        }
        if (!bean.getPassword().matches(".*\\d.*")) {
            throw new RegistrationException("La password deve contenere almeno un numero.");
        }
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }
    }

    private void validateRole(RegistrationBean bean) throws RegistrationException {
        if (bean.getRole() == null) {
            throw new RegistrationException("Seleziona un ruolo.");
        }
    }

    private void validateTutorFields(RegistrationBean bean) throws RegistrationException {
        if (bean.getRole() != Role.TUTOR) return;
        validateRequiredField(bean.getBio(), "La bio è obbligatoria per i tutor.");
        if (bean.getSubjects() == null || bean.getSubjects().isEmpty()) {
            throw new RegistrationException("Seleziona almeno una materia.");
        }
    }
}