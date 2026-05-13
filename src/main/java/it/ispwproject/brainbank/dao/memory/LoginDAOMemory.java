package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.dao.LoginDAO;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.model.Credentials;
import it.ispwproject.brainbank.model.User;

public class LoginDAOMemory implements LoginDAO {

    @Override
    public Credentials execute(String email, String plainPassword) throws LoginException {
        DemoDataStore store = DemoDataStore.getInstance();

        User user = store.getUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new LoginException("Credenziali non valide. Riprova."));

        if (plainPassword == null || plainPassword.isBlank()) {
            throw new LoginException("Credenziali non valide. Riprova.");
        }

        return new Credentials(email, plainPassword, user.getRole());
    }
}