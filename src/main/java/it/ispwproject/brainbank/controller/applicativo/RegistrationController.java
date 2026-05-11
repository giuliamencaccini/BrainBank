package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.dao.RegistrationDAO;
import it.ispwproject.brainbank.dao.SubjectDAO;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.RegistrationException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.model.Subject;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegistrationController {

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final RegistrationDAO registrationDAO;
    private final SubjectDAO subjectDAO;

    public RegistrationController() {
        this.registrationDAO = DAOFactory.getRegistrationDAO();
        this.subjectDAO      = DAOFactory.getSubjectDAO();
    }

    public List<SubjectBean> getAvailableSubjects() throws DAOException {
        List<SubjectBean> result = new ArrayList<>();
        for (Subject s : subjectDAO.getAll()) {
            result.add(new SubjectBean(s.getId(), s.getName()));
        }
        return result;
    }

    public void register(RegistrationBean bean) throws DAOException, RegistrationException {
        validateBean(bean);

        if (registrationDAO.emailExists(bean.getEmail())) {
            throw new RegistrationException("Email già registrata. Usa un'altra email.");
        }

        String hashedPassword = hashPassword(bean.getPassword());

        User user;
        if (bean.getRole() == Role.TUTOR) {
            user = new Tutor(0, bean.getName(), bean.getSurname(),
                    bean.getEmail(), hashedPassword, bean.getBio());
        } else {
            user = new Student(0, bean.getName(), bean.getSurname(),
                    bean.getEmail(), hashedPassword);
        }

        // Converte List<SubjectBean> in List<Integer> per il DAO
        List<Integer> subjectIds = new ArrayList<>();
        if (bean.getSubjects() != null) {
            for (SubjectBean s : bean.getSubjects()) {
                subjectIds.add(s.getId());
            }
        }

        registrationDAO.save(user, bean.getBio(), subjectIds);
    }

    private void validateBean(RegistrationBean bean) throws RegistrationException {
        if (bean.getName() == null || bean.getName().isBlank()) {
            throw new RegistrationException("Il nome è obbligatorio.");
        }
        if (bean.getSurname() == null || bean.getSurname().isBlank()) {
            throw new RegistrationException("Il cognome è obbligatorio.");
        }
        if (bean.getEmail() == null || bean.getEmail().isBlank()) {
            throw new RegistrationException("L'email è obbligatoria.");
        }
        if (!EMAIL_PATTERN.matcher(bean.getEmail()).matches()) {
            throw new RegistrationException("Email non valida.");
        }
        if (bean.getPassword() == null || bean.getPassword().length() < 8) {
            throw new RegistrationException("La password deve essere di almeno 8 caratteri.");
        }
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }
        if (bean.getRole() == null) {
            throw new RegistrationException("Seleziona un ruolo.");
        }
        if (bean.getRole() == Role.TUTOR) {
            if (bean.getBio() == null || bean.getBio().isBlank()) {
                throw new RegistrationException("La bio è obbligatoria per i tutor.");
            }
            if (bean.getSubjects() == null || bean.getSubjects().isEmpty()) {
                throw new RegistrationException("Seleziona almeno una materia.");
            }
        }
    }

    private String hashPassword(String password) throws RegistrationException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RegistrationException("Errore interno durante la codifica della password.", e);
        }
    }
}