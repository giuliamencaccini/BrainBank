package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.dao.*;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.NotificationException;
import it.ispwproject.brainbank.model.*;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingController {

    private static final String MEET_LINK_BASE = "https://meet.jit.si/brainbank-";

    private final BookingDAO  bookingDAO;
    private final SubjectDAO  subjectDAO;
    private final TutorDAO    tutorDAO;
    private final TimeSlotDAO timeSlotDAO;

    public BookingController() {
        this.bookingDAO  = DAOFactory.getBookingDAO();
        this.subjectDAO  = DAOFactory.getSubjectDAO();
        this.tutorDAO    = DAOFactory.getTutorDAO();
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
    }

    // ================================================================== //
    //  Use case: BookLesson
    // ================================================================== //

    public List<SubjectBean> getAvailableSubjects() throws DAOException {
        List<SubjectBean> result = new ArrayList<>();
        for (Subject subject : subjectDAO.getAll()) {
            result.add(new SubjectBean(subject.getId(), subject.getName()));
        }
        return result;
    }

    public List<TutorBean> getTutorsBySubject(SubjectBean subjectBean) throws DAOException {
        Subject subject = new Subject(subjectBean.getId(), subjectBean.getName());
        List<TutorBean> result = new ArrayList<>();

        User user = SessionManager.getInstance().getLoggedUser();
        if (!(user instanceof Student student)) {
            throw new DAOException("Utente non è uno studente.");
        }

        for (Tutor tutor : tutorDAO.getBySubject(subject)) {
            boolean favourite = student.hasFavourite(tutor.getId());
            result.add(new TutorBean(tutor.getId(), tutor.getName(),
                    tutor.getSurname(), tutor.getBio(), favourite));
        }

        return result;
    }

    public List<TimeSlotBean> getTutorAvailability(TutorBean tutorBean) throws DAOException {
        Tutor tutor = new Tutor(tutorBean.getId(), tutorBean.getName(),
                tutorBean.getSurname(), null, null, tutorBean.getBio());
        List<TimeSlotBean> result = new ArrayList<>();

        for (TimeSlot slot : timeSlotDAO.getAvailableByTutor(tutor)) {
            result.add(new TimeSlotBean(slot.getId(), slot.getDate(),
                    slot.getStartTime(), slot.getEndTime(), slot.isAvailable()));
        }

        return result;
    }

    public BookingResponseBean prepareBookingSummary(BookingRequestBean request) throws DAOException {
        Tutor    tutor   = tutorDAO.findById(request.getTutor().getId());
        Subject  subject = subjectDAO.findById(request.getSubject().getId());
        TimeSlot slot    = timeSlotDAO.findById(request.getTimeSlot().getId());

        if (tutor   == null) throw new DAOException("Tutor non trovato.");
        if (subject == null) throw new DAOException("Materia non trovata.");
        if (slot    == null) throw new DAOException("Slot non trovato.");

        return new BookingResponseBean(
                0, "PENDING", null,
                new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), false),
                new SubjectBean(subject.getId(), subject.getName()),
                new TimeSlotBean(slot.getId(), slot.getDate(),
                        slot.getStartTime(), slot.getEndTime(), slot.isAvailable())
        );
    }

    public BookingResponseBean createBooking(BookingRequestBean request)
            throws DAOException, BookingException {

        Student  student = (Student) SessionManager.getInstance().getLoggedUser();
        Tutor    tutor   = tutorDAO.findById(request.getTutor().getId());
        Subject  subject = subjectDAO.findById(request.getSubject().getId());
        TimeSlot slot    = timeSlotDAO.findById(request.getTimeSlot().getId());

        if (tutor   == null) throw new DAOException("Tutor non trovato.");
        if (subject == null) throw new DAOException("Materia non trovata.");
        if (slot    == null) throw new DAOException("Slot non trovato.");

        Booking booking = new Booking(student, tutor, subject, slot);
        booking.setMeetLink(MEET_LINK_BASE + UUID.randomUUID().toString().substring(0, 8));
        booking.confirm();

        bookingDAO.save(booking);

        BookingResponseBean response = new BookingResponseBean(
                booking.getId(),
                booking.getStatus().name(),
                booking.getMeetLink(),
                new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), false),
                new SubjectBean(subject.getId(), subject.getName()),
                new TimeSlotBean(slot.getId(), slot.getDate(),
                        slot.getStartTime(), slot.getEndTime(), slot.isAvailable())
        );

        try {
            NotificationController.sendBookingConfirmation(
                    student.getEmail(), student.getFullName(), response);
        } catch (NotificationException e) {
            System.out.println("  ⚠️ Notifica email non inviata: " + e.getMessage());
        }

        return response;
    }

    // ================================================================== //
    //  Use case: ViewBookings
    // ================================================================== //

    public List<BookingResponseBean> getStudentBookings(int studentId)
            throws DAOException, BookingException {
        List<BookingResponseBean> result = new ArrayList<>();

        for (Booking booking : bookingDAO.findByStudent(studentId)) {
            Tutor    tutor   = booking.getTutor();
            Subject  subject = booking.getSubject();
            TimeSlot slot    = booking.getTimeSlot();

            if (tutor == null || subject == null || slot == null) continue;

            result.add(new BookingResponseBean(
                    booking.getId(),
                    booking.getStatus().name(),
                    booking.getMeetLink(),
                    new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), false),
                    new SubjectBean(subject.getId(), subject.getName()),
                    new TimeSlotBean(slot.getId(), slot.getDate(),
                            slot.getStartTime(), slot.getEndTime(), slot.isAvailable())
            ));
        }

        return result;
    }

    // ================================================================== //
    //  Use case: CancelBooking
    // ================================================================== //

    public void cancelBooking(int bookingId, int studentId)
            throws DAOException, BookingException {

        List<BookingResponseBean> bookings = getStudentBookings(studentId);
        BookingResponseBean toCancel = bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);

        bookingDAO.cancel(bookingId, studentId);

        if (toCancel != null) {
            try {
                User user = SessionManager.getInstance().getLoggedUser();
                NotificationController.sendBookingCancellation(
                        user.getEmail(), user.getFullName(), toCancel);
            } catch (NotificationException e) {
                System.out.println("  ⚠️ Notifica email non inviata: " + e.getMessage());
            }
        }
    }
}