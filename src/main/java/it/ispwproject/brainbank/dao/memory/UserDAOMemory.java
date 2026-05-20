package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.UserDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;
import it.ispwproject.brainbank.util.singleton.SessionManager;

public class UserDAOMemory implements UserDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public User findByEmail(String email) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato: " + email));
    }

    @Override
    public void updateEmail(int id, String newEmail) throws DAOException {
        store.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato (ID: " + id + ")"))
                .setEmail(newEmail);
        SessionManager.getInstance().getLoggedUser().setEmail(newEmail);
    }
}