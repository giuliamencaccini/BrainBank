package it.ispwproject.brainbank.dao.memory;

import it.ispwproject.brainbank.controller.demo.DemoDataStore;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.model.Credentials;
import it.ispwproject.brainbank.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginDAOMemory {

    private LoginDAOMemory() {}

    public static Credentials execute(String email, String plainPassword) throws LoginException {
        DemoDataStore store = DemoDataStore.getInstance();

        User user = store.getUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new LoginException("Credenziali non valide. Riprova."));

        // In demo accettiamo qualsiasi password non vuota
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new LoginException("Credenziali non valide. Riprova.");
        }

        Role role = user.getRole();
        return new Credentials(email, plainPassword, role);
    }

    private static String hashPassword(String password) throws LoginException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new LoginException("Errore interno.", e);
        }
    }
}