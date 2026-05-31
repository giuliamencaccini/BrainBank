package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.dao.*;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.model.*;
import it.ispwproject.brainbank.pattern.observer.BookingCancellationObserver;
import it.ispwproject.brainbank.pattern.observer.BookingConfirmationObserver;
import it.ispwproject.brainbank.service.MeetLinkService;
import it.ispwproject.brainbank.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingController {

    private final BookingDAO  bookingDAO;
    private final SubjectDAO  subjectDAO;
    private final TutorDAO    tutorDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final StudentDAO studentDAO;

    public BookingController() {
        this.bookingDAO  = DAOFactory.getBookingDAO();
        this.subjectDAO  = DAOFactory.getSubjectDAO();
        this.tutorDAO    = DAOFactory.getTutorDAO();
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
        this.studentDAO  = DAOFactory.getStudentDAO();
    }

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
            boolean favourite =
                    studentDAO.isFavouriteTutor(student.getId(), tutor.getId());
            result.add(new TutorBean(tutor.getId(), tutor.getName(),
                    tutor.getSurname(), tutor.getBio(), tutor.getEmail(), favourite));
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

    private static final int RESERVATION_MINUTES = 3;

    public BookingResponseBean prepareBookingSummary(BookingRequestBean request)
            throws DAOException, BookingException {
        Tutor tutor   = tutorDAO.findById(request.getTutor().getId());
        Subject  subject = subjectDAO.findById(request.getSubject().getId());
        TimeSlot slot    = timeSlotDAO.findById(request.getTimeSlot().getId());

        if (tutor   == null) throw new DAOException("Tutor non trovato.");
        if (subject == null) throw new DAOException("Materia non trovata.");
        if (slot    == null) throw new DAOException("Slot non trovato.");

        boolean reserved = timeSlotDAO.reserveSlot(slot.getId(), RESERVATION_MINUTES);
        if (!reserved) throw new BookingException(
                "Lo slot è stato appena prenotato da un altro studente. Seleziona un altro slot.");

        return new BookingResponseBean(0, "PENDING", null,
                new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), tutor.getEmail(), false),
                new SubjectBean(subject.getId(), subject.getName()),
                new TimeSlotBean(slot.getId(), slot.getDate(),
                        slot.getStartTime(), slot.getEndTime(), slot.isAvailable()));
    }

    public void releaseSlot(int slotId) throws DAOException {
        timeSlotDAO.releaseSlot(slotId);
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

        for (Booking b : bookingDAO.findByStudent(student.getId())) {
            if (b.getStatus().name().equals("CANCELLED")) continue;
            TimeSlot existing = b.getTimeSlot();
            if (existing != null && slot.overlaps(existing)) {
                throw new BookingException(
                        "Hai già una prenotazione sovrapposta: " +
                                existing.getDate() + " " +
                                existing.getStartTime() + " – " + existing.getEndTime());
            }
        }

        Booking booking = new Booking(student, tutor, subject, slot);
        booking.setMeetLink(MeetLinkService.generate());
        booking.attach(new BookingConfirmationObserver(booking));
        booking.confirm();
        bookingDAO.save(booking);

        return new BookingResponseBean(
                booking.getId(), booking.getStatus().name(), booking.getMeetLink(),
                new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), tutor.getEmail(), false),
                new SubjectBean(subject.getId(), subject.getName()),
                new TimeSlotBean(slot.getId(), slot.getDate(),
                        slot.getStartTime(), slot.getEndTime(), slot.isAvailable()));
    }

    public List<BookingResponseBean> getStudentBookings(int studentId)
            throws DAOException{
        List<BookingResponseBean> result = new ArrayList<>();

        for (Booking booking : bookingDAO.findByStudent(studentId)) {
            Tutor    tutor   = booking.getTutor();
            Subject  subject = booking.getSubject();
            TimeSlot slot    = booking.getTimeSlot();

            if (tutor == null || subject == null || slot == null) continue;

            result.add(new BookingResponseBean(
                    booking.getId(), booking.getStatus().name(), booking.getMeetLink(),
                    new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), tutor.getEmail(), false),
                    new SubjectBean(subject.getId(), subject.getName()),
                    new TimeSlotBean(slot.getId(), slot.getDate(),
                            slot.getStartTime(), slot.getEndTime(), slot.isAvailable())));
        }

        return result;
    }

    public List<BookingResponseBean> getStudentPastBookings(int studentId) throws DAOException {
        List<BookingResponseBean> result = new ArrayList<>();
        for (Booking booking : bookingDAO.findPastByStudent(studentId)) {
            Tutor    tutor   = booking.getTutor();
            Subject  subject = booking.getSubject();
            TimeSlot slot    = booking.getTimeSlot();
            if (tutor == null || subject == null || slot == null) continue;
            result.add(new BookingResponseBean(
                    booking.getId(), booking.getStatus().name(), booking.getMeetLink(),
                    new TutorBean(tutor.getId(), tutor.getName(), tutor.getSurname(), tutor.getBio(), tutor.getEmail(), false),
                    new SubjectBean(subject.getId(), subject.getName()),
                    new TimeSlotBean(slot.getId(), slot.getDate(),
                            slot.getStartTime(), slot.getEndTime(), slot.isAvailable())));
        }
        return result;
    }

    public void cancelBooking(int bookingId, int studentId) throws DAOException {

        List<Booking> bookings = bookingDAO.findByStudent(studentId);
        Booking booking = bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (booking != null) {
            booking.attach(new BookingCancellationObserver(booking));
        }

        bookingDAO.cancel(bookingId, studentId);
    }

    public void addTutorToFavourites(int studentId, int tutorId)
            throws DAOException {

        studentDAO.addFavouriteTutor(studentId, tutorId);
    }

    public void removeTutorFromFavourites(int studentId, int tutorId)
            throws DAOException {

        studentDAO.removeFavouriteTutor(studentId, tutorId);
    }
}