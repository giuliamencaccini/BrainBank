package it.ispwproject.brainbank.controller.demo;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.controller.applicativo.BookingController;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingControllerDemo extends BookingController {

    private static final String DEMO_MEET_LINK = "https://meet.jit.si/brainbank-demo";

    private final List<SubjectBean>  demoSubjects = new ArrayList<>();
    private final List<TutorBean>    demoTutors   = new ArrayList<>();
    private final List<TimeSlotBean> demoSlots    = new ArrayList<>();
    private final List<BookingResponseBean> demoBookings = new ArrayList<>();
    private int nextBookingId = 1;

    public BookingControllerDemo() {
        initDemoData();
    }

    private void initDemoData() {
        demoSubjects.add(new SubjectBean(1, "Analisi 1"));
        demoSubjects.add(new SubjectBean(2, "Fisica 1"));
        demoSubjects.add(new SubjectBean(3, "Algebra"));

        demoTutors.add(new TutorBean(1, "Gabriele", "Bianchi",
                "Laurea in Matematica, 5 anni di esperienza", false));
        demoTutors.add(new TutorBean(2, "Sofia", "Ferrari",
                "Laurea in Fisica, specializzata in meccanica quantistica", false));

        demoSlots.add(new TimeSlotBean(1, LocalDate.now().plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(11, 0), true));
        demoSlots.add(new TimeSlotBean(2, LocalDate.now().plusDays(1),
                LocalTime.of(11, 0), LocalTime.of(13, 0), true));
        demoSlots.add(new TimeSlotBean(3, LocalDate.now().plusDays(2),
                LocalTime.of(14, 0), LocalTime.of(16, 0), true));
    }

    @Override
    public List<SubjectBean> getAvailableSubjects() throws DAOException {
        return new ArrayList<>(demoSubjects);
    }

    @Override
    public List<TutorBean> getTutorsBySubject(SubjectBean subjectBean) throws DAOException {
        return new ArrayList<>(demoTutors);
    }

    @Override
    public List<TimeSlotBean> getTutorAvailability(TutorBean tutorBean) throws DAOException {
        return demoSlots.stream()
                .filter(TimeSlotBean::isAvailable)
                .toList();
    }

    @Override
    public BookingResponseBean prepareBookingSummary(BookingRequestBean request) throws DAOException {
        TutorBean    tutor   = findTutorById(request.getTutor().getId());
        SubjectBean  subject = findSubjectById(request.getSubject().getId());
        TimeSlotBean slot    = findSlotById(request.getTimeSlot().getId());

        return new BookingResponseBean(0, "PENDING", null, tutor, subject, slot);
    }

    @Override
    public BookingResponseBean createBooking(BookingRequestBean request)
            throws DAOException, BookingException {
        TutorBean    tutor   = findTutorById(request.getTutor().getId());
        SubjectBean  subject = findSubjectById(request.getSubject().getId());
        TimeSlotBean slot    = findSlotById(request.getTimeSlot().getId());

        slot.setAvailable(false);

        BookingResponseBean booking = new BookingResponseBean(
                nextBookingId++, "CONFIRMED", DEMO_MEET_LINK,
                tutor, subject, slot);

        demoBookings.add(booking);
        return booking;
    }

    @Override
    public List<BookingResponseBean> getStudentBookings(int studentId)
            throws DAOException, BookingException {
        return new ArrayList<>(demoBookings);
    }

    @Override
    public void cancelBooking(int bookingId, int studentId)
            throws DAOException, BookingException {
        BookingResponseBean booking = demoBookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new BookingException(
                        "Prenotazione non trovata (ID: " + bookingId + ")"));

        booking.setStatus("CANCELLED");
        booking.getTimeSlot().setAvailable(true);
    }

    private TutorBean findTutorById(int id) throws DAOException {
        return demoTutors.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Tutor non trovato (ID: " + id + ")"));
    }

    private SubjectBean findSubjectById(int id) throws DAOException {
        return demoSubjects.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Materia non trovata (ID: " + id + ")"));
    }

    private TimeSlotBean findSlotById(int id) throws DAOException {
        return demoSlots.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Slot non trovato (ID: " + id + ")"));
    }
}