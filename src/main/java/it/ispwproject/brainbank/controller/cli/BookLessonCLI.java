package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.controller.demo.DemoFactory;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.util.singleton.SessionManager;
import it.ispwproject.brainbank.view.BookLessonView;

import java.util.ArrayList;
import java.util.List;

public class BookLessonCLI {

    private final BookingController bookingController = DemoFactory.getBookingController();
    private final BookLessonView view = new BookLessonView();

    public CLIState start() {
        view.mostraIntestazione();

        // Costruisce StudentBean dallo studente loggato
        Student loggedStudent = (Student) SessionManager.getInstance().getLoggedUser();
        StudentBean studentBean = new StudentBean(
                loggedStudent.getId(), loggedStudent.getName(),
                loggedStudent.getSurname(), loggedStudent.getEmail());

        try {
            // Step 1 – materia
            List<SubjectBean> subjects = bookingController.getAvailableSubjects();
            if (subjects.isEmpty()) {
                view.mostraMessaggio("Nessuna materia disponibile.");
                return CLIState.DASHBOARD_STUDENT;
            }
            view.mostraMaterie(subjects);
            int sc = view.chiediScelta("Seleziona una materia", 0, subjects.size());
            if (sc == 0) return CLIState.DASHBOARD_STUDENT;
            SubjectBean subject = subjects.get(sc - 1);

            // Step 2 – tutor
            List<TutorBean> allTutors = bookingController.getTutorsBySubject(subject);
            if (allTutors.isEmpty()) {
                view.mostraMessaggio("Nessun tutor disponibile per questa materia.");
                return CLIState.DASHBOARD_STUDENT;
            }
            List<TutorBean> favourites = allTutors.stream().filter(TutorBean::isFavourite).toList();
            List<TutorBean> others     = allTutors.stream().filter(t -> !t.isFavourite()).toList();
            view.mostraTutor(favourites, others);
            int tc = view.chiediScelta("Seleziona un tutor", 0, allTutors.size());
            if (tc == 0) return CLIState.DASHBOARD_STUDENT;
            List<TutorBean> ordered = new ArrayList<>(favourites);
            ordered.addAll(others);
            TutorBean tutor = ordered.get(tc - 1);

            // Step 3 – slot
            List<TimeSlotBean> available = bookingController.getTutorAvailability(tutor)
                    .stream().filter(TimeSlotBean::isAvailable).toList();
            if (available.isEmpty()) {
                view.mostraMessaggio("Nessuno slot disponibile per questo tutor.");
                return CLIState.DASHBOARD_STUDENT;
            }
            view.mostraSlot(available);
            int slc = view.chiediScelta("Seleziona uno slot", 0, available.size());
            if (slc == 0) return CLIState.DASHBOARD_STUDENT;
            TimeSlotBean slot = available.get(slc - 1);

            // Step 4 – riepilogo
            BookingRequestBean request = new BookingRequestBean(studentBean, tutor, subject, slot);
            BookingResponseBean summary = bookingController.prepareBookingSummary(request);
            view.mostraRiepilogo(summary);

            if (!view.chiediConferma("Confermare?")) {
                view.mostraMessaggio("Prenotazione annullata.");
                return CLIState.DASHBOARD_STUDENT;
            }

            // Step 5 – creazione
            BookingResponseBean response = bookingController.createBooking(request);
            view.mostraConferma(response);

        } catch (DAOException | BookingException e) {
            view.mostraMessaggio("❌ Errore: " + e.getMessage());
        }

        return CLIState.DASHBOARD_STUDENT;
    }
}