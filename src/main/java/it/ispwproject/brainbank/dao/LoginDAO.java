package it.ispwproject.brainbank.dao;

import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.LoginException;
import it.ispwproject.brainbank.model.Credentials;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginDAO {

    private LoginDAO() {}

    public static Credentials execute(String email, String plainPassword) throws LoginException {
        String hashedPassword = hashPassword(plainPassword);

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call login(?, ?, ?, ?, ?, ?)}")) {

            cs.setString(1, email);
            cs.setString(2, hashedPassword);
            cs.registerOutParameter(3, Types.INTEGER); // p_id
            cs.registerOutParameter(4, Types.VARCHAR); // p_name
            cs.registerOutParameter(5, Types.VARCHAR); // p_surname
            cs.registerOutParameter(6, Types.VARCHAR); // p_role

            cs.execute();

            String roleStr = cs.getString(6);

            if (roleStr == null || roleStr.equals("NOT_FOUND")) {
                throw new LoginException("Credenziali non valide. Riprova.");
            }

            Role role = Role.valueOf(roleStr.toUpperCase());

            return new Credentials(email, hashedPassword, role);

        } catch (SQLException e) {
            throw new LoginException("Errore DB durante il login: " + e.getMessage(), e);
        }
    }

    private static String hashPassword(String password) throws LoginException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new LoginException("Errore interno durante la codifica della password.", e);
        }
    }
}