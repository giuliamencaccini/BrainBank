package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.dao.UserDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.util.regex.Pattern;

public class UserController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updateEmail(String newEmail) throws DAOException {
        if (newEmail == null || newEmail.isBlank())
            throw new DAOException("L'email non può essere vuota.");
        if (!EMAIL_PATTERN.matcher(newEmail).matches())
            throw new DAOException("Email non valida.");

        int id = SessionManager.getInstance().getLoggedUser().getId();
        userDAO.updateEmail(id, newEmail);
        SessionManager.getInstance().getLoggedUser().setEmail(newEmail);
    }
}