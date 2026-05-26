package it.ispwproject.brainbank.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.ispwproject.brainbank.exception.LoginException;

public final class PasswordUtils {

    private PasswordUtils() {}

    public static String hash(String password) throws LoginException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new LoginException("Errore interno durante la codifica della password.", e);
        }
    }
}