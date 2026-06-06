package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.exception.AvailabilityException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Tutor;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;


import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : AvailabilityControllerTest
 * Author     : Giulia Mencaccini
 * Description: Verifica che un tutor non possa aggiungere due
 *              slot con orari sovrapposti. Il primo slot viene
 *              aggiunto con successo, mentre il secondo deve
 *              lanciare una AvailabilityException.
 * ------------------------------------------------------------
 */

class AvailabilityControllerTest {

    private AvailabilityController availabilityController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);

        // Simula tutor loggato
        Tutor tutor = new Tutor(3, "Demo", "Tutor", "tutor@demo", null, "Tutor demo");
        SessionManager.getInstance().setLoggedUser(tutor);

        availabilityController = new AvailabilityController();
    }

    @Test
    void testSlotSovrapposto() throws DAOException, AvailabilityException {
        LocalDate domani = LocalDate.of(2030, java.time.Month.JUNE, 16);

        // Primo slot — deve andare a buon fine
        TimeSlotBean slot1 = new TimeSlotBean(0, domani,
                LocalTime.of(9, 0), LocalTime.of(11, 0), true);
        availabilityController.addSlot(slot1);

        // Secondo slot con orario sovrapposto — deve lanciare AvailabilityException
        TimeSlotBean slot2 = new TimeSlotBean(0, domani,
                LocalTime.of(10, 0), LocalTime.of(12, 0), true);

        assertThrows(AvailabilityException.class, () ->
                availabilityController.addSlot(slot2)
        );
    }
}