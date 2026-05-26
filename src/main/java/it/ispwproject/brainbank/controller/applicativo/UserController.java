package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.dao.UserDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.util.ValidationUtils;
import it.ispwproject.brainbank.util.singleton.SessionManager;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updateEmail(String newEmail) throws DAOException {
        if (newEmail == null || newEmail.isBlank())
            throw new DAOException("L'email non può essere vuota.");
        if (!ValidationUtils.isValidEmail(newEmail))
            throw new DAOException("Email non valida.");

        int id = SessionManager.getInstance().getLoggedUser().getId();
        userDAO.updateEmail(id, newEmail);
        SessionManager.getInstance().getLoggedUser().setEmail(newEmail);
    }
}