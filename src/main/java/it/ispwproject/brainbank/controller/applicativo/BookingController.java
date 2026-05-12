package it.ispwproject.brainbank.controller.applicativo;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.dao.*;
import it.ispwproject.brainbank.exception.BookingException;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.exception.NotificationException;
import it.ispwproject.brainbank.model.*;
import it.ispwproject.brainbank.util.logger.AppLogger;
import it.ispwproject.brainbank.util.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingController {

    private static final String MEET_LINK_BASE = "https://meet.jit.si/brainbank-";

    private final BookingDAO bookingDAO;
    private final SubjectDAO subjectDAO;
    private final TutorDAO tutorDAO;
    private final TimeSlotDAO timeSlotDAO;

    public BookingController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
        this.subjectDAO = DAOFactory.getSubjectDAO();
        this.tutorDAO = DAOFactory.getTutorDAO();
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
    }

    public List<SubjectBean> getAvailableSubjects() throws DAOException {
        List<SubjectBean> result = new ArrayList<>();

        for (Subject subject : subjectDAO.getAll()) {
            result.add(new SubjectBean(subject.getId(), subject.getName()));
        }

        return result;
    }

    public List<TutorBean> getTutorsBySubject(SubjectBean subjectBean)
            throws DAOException, BookingException {

        if (subjectBean == null) {
            throw new BookingException("Invalid subject.");
        }

        User user = SessionManager.getInstance().getLoggedUser();

        if (!(user instanceof Student student)) {
            throw new BookingException("Only students can view tutors by subject.");
        }

        Subject subject = new Subject(subjectBean.getId(), subjectBean.getName());
        List<TutorBean> result = new ArrayList<>();

        for (Tutor tutor : tutorDAO.getBySubject(subject)) {
            boolean favourite = student.hasFavourite(tutor.getId());

            result.add(new TutorBean(
                    tutor.getId(),
                    tutor.getName(),
                    tutor.getSurname(),
                    tutor.getBio(),
                    favourite
            ));
        }

        return result;
    }

    public List<TimeSlotBean> getTutorAvailability(TutorBean tutorBean)
            throws DAOException, BookingException {

        if (tutorBean == null) {
            throw new BookingException("Invalid tutor.");
        }

        Tutor tutor = new Tutor(
                tutorBean.getId(),
                tutorBean.getName(),
                tutorBean.getSurname(),
                null,
                null,
                tutorBean.getBio()
        );

        List<TimeSlotBean> result = new ArrayList<>();

        for (TimeSlot slot : timeSlotDAO.getAvailableByTutor(tutor)) {
            result.add(new TimeSlotBean(
                    slot.getId(),
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.isAvailable()
            ));
        }

        return result;
    }

    public BookingResponseBean prepareBookingSummary(BookingRequestBean request)
            throws DAOException, BookingException {

        validateBookingRequest(request);

        Tutor tutor = tutorDAO.findById(request.getTutor().getId());
        Subject subject = subjectDAO.findById(request.getSubject().getId());
        TimeSlot slot = timeSlotDAO.findById(request.getTimeSlot().getId());

        if (tutor == null) {
            throw new BookingException("Tutor not found.");
        }

        if (subject == null) {
            throw new BookingException("Subject not found.");
        }

        if (slot == null) {
            throw new BookingException("Time slot not found.");
        }

        if (!slot.isAvailable()) {
            throw new BookingException("Selected time slot is no longer available.");
        }

        return new BookingResponseBean(
                0,
                "PENDING",
                null,
                new TutorBean(
                        tutor.getId(),
                        tutor.getName(),
                        tutor.getSurname(),
                        tutor.getBio(),
                        false
                ),
                new SubjectBean(
                        subject.getId(),
                        subject.getName()
                ),
                new TimeSlotBean(
                        slot.getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.isAvailable()
                )
        );
    }

    public BookingResponseBean createBooking(BookingRequestBean request)
            throws DAOException, BookingException {

        User loggedUser = SessionManager.getInstance().getLoggedUser();

        if (!(loggedUser instanceof Student student)) {
            throw new BookingException("Only students can book a lesson.");
        }

        validateBookingRequest(request);

        Tutor tutor = tutorDAO.findById(request.getTutor().getId());
        Subject subject = subjectDAO.findById(request.getSubject().getId());
        TimeSlot slot = timeSlotDAO.findById(request.getTimeSlot().getId());

        if (tutor == null) {
            throw new BookingException("Tutor not found.");
        }

        if (subject == null) {
            throw new BookingException("Subject not found.");
        }

        if (slot == null) {
            throw new BookingException("Time slot not found.");
        }

        if (!slot.isAvailable()) {
            throw new BookingException("Selected time slot is no longer available.");
        }

        Booking booking = new Booking(student, tutor, subject, slot);
        booking.setMeetLink(MEET_LINK_BASE + UUID.randomUUID().toString().substring(0, 8));
        booking.confirm();

        bookingDAO.save(booking);

        BookingResponseBean response = new BookingResponseBean(
                booking.getId(),
                booking.getStatus().name(),
                booking.getMeetLink(),
                new TutorBean(
                        tutor.getId(),
                        tutor.getName(),
                        tutor.getSurname(),
                        tutor.getBio(),
                        false
                ),
                new SubjectBean(
                        subject.getId(),
                        subject.getName()
                ),
                new TimeSlotBean(
                        slot.getId(),
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.isAvailable()
                )
        );

        try {
            NotificationController.sendBookingConfirmation(
                    student.getEmail(),
                    student.getFullName(),
                    response
            );
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica email non inviata: " + e.getMessage());
        }

        return response;
    }

    public List<BookingResponseBean> getStudentBookings(int studentId)
            throws DAOException {

        List<BookingResponseBean> result = new ArrayList<>();

        for (Booking booking : bookingDAO.findByStudent(studentId)) {
            Tutor tutor = booking.getTutor();
            Subject subject = booking.getSubject();
            TimeSlot slot = booking.getTimeSlot();

            if (tutor == null || subject == null || slot == null) {
                continue;
            }

            result.add(new BookingResponseBean(
                    booking.getId(),
                    booking.getStatus().name(),
                    booking.getMeetLink(),
                    new TutorBean(
                            tutor.getId(),
                            tutor.getName(),
                            tutor.getSurname(),
                            tutor.getBio(),
                            false
                    ),
                    new SubjectBean(
                            subject.getId(),
                            subject.getName()
                    ),
                    new TimeSlotBean(
                            slot.getId(),
                            slot.getDate(),
                            slot.getStartTime(),
                            slot.getEndTime(),
                            slot.isAvailable()
                    )
            ));
        }

        return result;
    }

    public void cancelBooking(int bookingId, int studentId)
            throws DAOException, BookingException {

        List<BookingResponseBean> bookings = getStudentBookings(studentId);

        BookingResponseBean toCancel = bookings.stream()
                .filter(booking -> booking.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (toCancel == null) {
            throw new BookingException("Booking not found for this student.");
        }

        bookingDAO.cancel(bookingId, studentId);

        try {
            User user = SessionManager.getInstance().getLoggedUser();

            NotificationController.sendBookingCancellation(
                    user.getEmail(),
                    user.getFullName(),
                    toCancel
            );
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica email non inviata: " + e.getMessage());
        }
    }

    private void validateBookingRequest(BookingRequestBean request)
            throws BookingException {

        if (request == null
                || request.getTutor() == null
                || request.getSubject() == null
                || request.getTimeSlot() == null) {

            throw new BookingException("Invalid booking request.");
        }
    }
}