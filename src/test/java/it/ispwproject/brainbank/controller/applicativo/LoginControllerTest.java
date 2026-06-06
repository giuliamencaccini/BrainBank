package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.exception.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : LoginControllerTest
 * Author     : Giulia Mencaccini
 * Description: Verifica la corretta gestione del login con
 *              credenziali non valide. Il sistema deve rifiutare
 *              l'accesso e lanciare una LoginException quando
 *              l'email non è registrata nella piattaforma.
 * ------------------------------------------------------------
 */

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setup() {
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        loginController = new LoginController();
    }

    @Test
    void testLoginConCredenzialiErrate() {
        // Tenta il login con un'email non registrata
        // Deve lanciare LoginException
        assertThrows(LoginException.class, () ->
                loginController.login("nonregistrato@demo", "password123")
        );
    }
}