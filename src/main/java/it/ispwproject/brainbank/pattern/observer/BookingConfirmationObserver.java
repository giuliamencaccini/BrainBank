package it.ispwproject.brainbank.pattern.observer;


import it.ispwproject.brainbank.bean.BookingResponseBean;
import it.ispwproject.brainbank.bean.SubjectBean;
import it.ispwproject.brainbank.bean.TimeSlotBean;
import it.ispwproject.brainbank.bean.TutorBean;
import it.ispwproject.brainbank.service.NotificationService;
import it.ispwproject.brainbank.model.Booking;
import it.ispwproject.brainbank.util.logger.AppLogger;

public class BookingConfirmationObserver implements Observer {

    private final Booking booking;

    public BookingConfirmationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        try {
            BookingResponseBean response = buildResponse();

            NotificationService.sendBookingConfirmation(
                    booking.getStudent().getEmail(),
                    booking.getStudent().getFullName(),
                    response);

        } catch (it.ispwproject.brainbank.exception.NotificationException e) {
            AppLogger.logWarning("Notifica conferma non inviata: " + e.getMessage());
        }
    }

    private BookingResponseBean buildResponse() {
        TutorBean tutorBean = new TutorBean(
                booking.getTutor().getId(),
                booking.getTutor().getName(),
                booking.getTutor().getSurname(),
                booking.getTutor().getBio(),
                booking.getTutor().getEmail(),
                false);

        SubjectBean subjectBean = new SubjectBean(
                booking.getSubject().getId(),
                booking.getSubject().getName());

        TimeSlotBean slotBean = new TimeSlotBean(
                booking.getTimeSlot().getId(),
                booking.getTimeSlot().getDate(),
                booking.getTimeSlot().getStartTime(),
                booking.getTimeSlot().getEndTime(),
                booking.getTimeSlot().isAvailable());

        return new BookingResponseBean(
                booking.getId(),
                booking.getStatus().name(),
                booking.getMeetLink(),
                tutorBean,
                subjectBean,
                slotBean);
    }
}