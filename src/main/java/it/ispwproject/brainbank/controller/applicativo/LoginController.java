package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.SessionBean;
import it.ispwproject.brainbank.dao.ConnectionFactory;
import it.ispwproject.brainbank.dao.LoginDAO;
import it.ispwproject.brainbank.dao.UserDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.model.Credentials;
import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.sql.SQLException;

public class LoginController {

    public enum LoginResult {
        SUCCESSO_STUDENT,
        SUCCESSO_TUTOR,
        SUCCESSO_ADMIN
    }

    public LoginResult login(String email, String password) throws LoginException {

        // Fase 1: autenticazione
        Credentials credentials = LoginDAO.execute(email, password);

        // Fase 2: cambia connessione al ruolo dell'utente loggato
        try {
            ConnectionFactory.changeRole(credentials.getRole());
        } catch (SQLException e) {
            throw new LoginException("Errore durante il cambio ruolo: " + e.getMessage(), e);
        }

        // Fase 3: carica User completo senza password
        User user;
        try {
            user = UserDAO.findByEmail(email);
        } catch (DAOException e) {
            throw new LoginException("Errore nel caricamento utente: " + e.getMessage(), e);
        }

        // Fase 4: salva in sessione
        SessionManager.getInstance().setLoggedUser(user);
        SessionManager.getInstance().setSessionBean(
                new SessionBean(user.getEmail(), credentials.getRole())
        );

        return switch (credentials.getRole()) {
            case STUDENT -> LoginResult.SUCCESSO_STUDENT;
            case TUTOR   -> LoginResult.SUCCESSO_TUTOR;
            case ADMIN   -> LoginResult.SUCCESSO_ADMIN;
        };
    }
}