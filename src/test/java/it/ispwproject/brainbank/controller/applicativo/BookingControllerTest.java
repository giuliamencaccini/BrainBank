package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.dao.DAOFactory;
import it.ispwproject.brainbank.demo.DemoDataStore;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : BookingControllerTest
 * Author     : Giulia Mencaccini
 * Description: Verifica il meccanismo di prenotazione temporanea
 *              degli slot. Comprende due scenari:
 *              1) Tentativo di prenotare uno slot già riservato
 *                 da un altro studente (test sequenziale).
 *              2) Due studenti che tentano di prenotare lo stesso
 *                 slot contemporaneamente (test di concorrenza).
 *              In entrambi i casi solo uno deve riuscire.
 * ------------------------------------------------------------
 */
class BookingControllerTest {

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
    }

    // ── Test 1: Prenotazione su slot già riservato (sequenziale) ──────

    @Test
    void testPrenotazioneSuSlotGiaRiservato() throws DAOException, BookingException {

        SubjectBean  subject = new SubjectBean(3, "Algebra");
        TutorBean    tutor   = new TutorBean(3, "Demo", "Tutor", null, null, false);
        TimeSlotBean slot    = new TimeSlotBean(3, null, null, null, true); // slot 3 — libero nel DemoDataStore

        // Studente 1 riserva lo slot
        Student s1 = new Student(1, "Demo", "Student", "student@demo", null);
        SessionManager.getInstance().setLoggedUser(s1);
        BookingController bc1 = new BookingController();
        StudentBean sb1 = new StudentBean(1, "Demo", "Student", "student@demo");
        bc1.prepareBookingSummary(new BookingRequestBean(sb1, tutor, subject, slot));

        // Studente 2 tenta lo stesso slot — deve ricevere BookingException
        Student s2 = new Student(2, "Emma", "Rossi", "emma@demo", null);
        SessionManager.getInstance().setLoggedUser(s2);
        BookingController bc2 = new BookingController();
        StudentBean sb2 = new StudentBean(2, "Emma", "Rossi", "emma@demo");

        assertThrows(BookingException.class, () ->
                bc2.prepareBookingSummary(new BookingRequestBean(sb2, tutor, subject, slot))
        );
    }

    // ── Test 2: Prenotazione concorrente (multi-thread) ───────────────

    @Test
    void testPrenotazioneConcorrente() throws InterruptedException {

        SubjectBean  subject = new SubjectBean(3, "Algebra");
        TutorBean    tutor   = new TutorBean(3, "Demo", "Tutor", null, null, false);
        TimeSlotBean slot    = new TimeSlotBean(4, null, null, null, true); // slot 4 — libero nel DemoDataStore

        CountDownLatch latch    = new CountDownLatch(1);
        AtomicInteger successi  = new AtomicInteger(0);
        AtomicInteger eccezioni = new AtomicInteger(0);

        Runnable studente1 = () -> {
            try {
                DAOFactory.setPersistence(DAOFactory.MEMORY);
                Student s1 = new Student(1, "Demo", "Student", "student@demo", null);
                SessionManager.getInstance().setLoggedUser(s1);
                BookingController bc = new BookingController();
                StudentBean sb = new StudentBean(1, "Demo", "Student", "student@demo");
                latch.await();
                bc.prepareBookingSummary(new BookingRequestBean(sb, tutor, subject, slot));
                successi.incrementAndGet();
            } catch (BookingException | DAOException e) {
                eccezioni.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable studente2 = () -> {
            try {
                DAOFactory.setPersistence(DAOFactory.MEMORY);
                Student s2 = new Student(2, "Emma", "Rossi", "emma@demo", null);
                SessionManager.getInstance().setLoggedUser(s2);
                BookingController bc = new BookingController();
                StudentBean sb = new StudentBean(2, "Emma", "Rossi", "emma@demo");
                latch.await();
                bc.prepareBookingSummary(new BookingRequestBean(sb, tutor, subject, slot));
                successi.incrementAndGet();
            } catch (BookingException | DAOException e) {
                eccezioni.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(studente1);
        Thread t2 = new Thread(studente2);
        t1.start();
        t2.start();
        latch.countDown();
        t1.join();
        t2.join();

        assertEquals(1, successi.get(),  "Esattamente uno studente deve prenotare");
        assertEquals(1, eccezioni.get(), "Esattamente uno studente deve ricevere eccezione");
    }
}