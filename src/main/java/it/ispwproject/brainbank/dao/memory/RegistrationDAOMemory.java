package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.RegistrationDAO;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.User;

import java.util.List;

public class RegistrationDAOMemory implements RegistrationDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public boolean emailExists(String email) throws DAOException {
        return store.getUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void save(User user, String bio, List<Integer> subjectIds) throws DAOException {
        user.setId(store.nextUserId());
        store.getUsers().add(user);
    }
}