package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.RegistrationBean;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.enumerator.Role;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.RegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica la corretta gestione della registrazione con email già esistente.
 */
class RegistrationControllerTest {

    private RegistrationController registrationController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        registrationController = new RegistrationController();
    }

    @Test
    void testRegistrazioneConEmailDuplicata() throws DAOException, RegistrationException {
        // Prima registrazione — deve andare a buon fine
        RegistrationBean bean = new RegistrationBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setEmail("mario@test.com");
        bean.setPassword("Password123");
        bean.setConfirmPassword("Password123");
        bean.setRole(Role.STUDENT);

        registrationController.register(bean);

        // Seconda registrazione con la stessa email — deve lanciare RegistrationException
        RegistrationBean duplicato = new RegistrationBean();
        duplicato.setName("Mario");
        duplicato.setSurname("Rossi");
        duplicato.setEmail("mario@test.com");
        duplicato.setPassword("Password123");
        duplicato.setConfirmPassword("Password123");
        duplicato.setRole(Role.STUDENT);

        assertThrows(RegistrationException.class, () ->
                registrationController.register(duplicato)
        );
    }
}