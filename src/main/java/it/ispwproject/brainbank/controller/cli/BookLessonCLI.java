package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.Student;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;
import it.ispwproject.brainbank.view.cli.BookLessonView;

import java.util.ArrayList;
import java.util.List;

public class BookLessonCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final BookLessonView view = new BookLessonView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        Student loggedStudent = (Student) SessionManager.getInstance().getLoggedUser();
        StudentBean studentBean = new StudentBean(
                loggedStudent.getId(), loggedStudent.getName(),
                loggedStudent.getSurname(), loggedStudent.getEmail());

        TimeSlotBean slot = null;

        try {
            // Step 1 – materia
            List<SubjectBean> subjects = bookingController.getAvailableSubjects();
            if (subjects.isEmpty()) {
                view.mostraMessaggio("Nessuna materia disponibile.");
                goBack(context); return;
            }
            view.mostraMaterie(subjects);
            int sc = view.chiediScelta("Seleziona una materia", 0, subjects.size());
            if (sc == 0) { goBack(context); return; }
            SubjectBean subject = subjects.get(sc - 1);

            // Step 2 – tutor
            List<TutorBean> allTutors = bookingController.getTutorsBySubject(subject);
            if (allTutors.isEmpty()) {
                view.mostraMessaggio("Nessun tutor disponibile per questa materia.");
                goBack(context); return;
            }
            List<TutorBean> favourites = allTutors.stream().filter(TutorBean::isFavourite).toList();
            List<TutorBean> others     = allTutors.stream().filter(t -> !t.isFavourite()).toList();
            view.mostraTutor(favourites, others);
            int tc = view.chiediScelta("Seleziona un tutor", 0, allTutors.size());
            if (tc == 0) { goBack(context); return; }
            List<TutorBean> ordered = new ArrayList<>(favourites);
            ordered.addAll(others);
            TutorBean tutor = ordered.get(tc - 1);

            // Step 3 – slot
            List<TimeSlotBean> available = bookingController.getTutorAvailability(tutor)
                    .stream().filter(TimeSlotBean::isAvailable).toList();
            if (available.isEmpty()) {
                view.mostraMessaggio("Nessuno slot disponibile per questo tutor.");
                goBack(context); return;
            }
            view.mostraSlot(available);
            int slc = view.chiediScelta("Seleziona uno slot", 0, available.size());
            if (slc == 0) { goBack(context); return; }
            slot = available.get(slc - 1);

            // Step 4 – riepilogo
            BookingRequestBean request = new BookingRequestBean(studentBean, tutor, subject, slot);
            BookingResponseBean summary = bookingController.prepareBookingSummary(request);
            view.mostraRiepilogo(summary);

            if (!view.chiediConferma("Confermare? (hai 3 minuti per decidere)")) {
                bookingController.releaseSlot(slot.getId());
                view.mostraMessaggio("Prenotazione annullata.");
                goBack(context); return;
            }

            // Step 5 – creazione
            BookingResponseBean response = bookingController.createBooking(request);
            view.mostraConferma(response);

            if (!tutor.isFavourite() && view.chiediConferma(
                    "Vuoi aggiungere " + tutor.getFullName() + " ai tutor preferiti?")) {
                bookingController.addTutorToFavourites(tutor.getId());
                view.mostraMessaggio("⭐ Tutor aggiunto ai preferiti.");
            }

        } catch (BookingException e) {
            if (slot != null) {
                try { bookingController.releaseSlot(slot.getId()); }
                catch (DAOException ex) { /* ignora */ }
            }
            view.mostraMessaggio("❌ Errore: " + e.getMessage());
        } catch (DAOException e) {
            view.mostraMessaggio("❌ Errore: " + e.getMessage());
        }

        goBack(context);
    }
}