package it.ispwproject.brainbank.pattern.observer;

import it.ispwproject.brainbank.bean.*;
import it.ispwproject.brainbank.service.NotificationService;
import it.ispwproject.brainbank.model.Booking;
import it.ispwproject.brainbank.util.logger.AppLogger;

public class BookingCancellationObserver implements Observer {

    private final Booking booking;

    public BookingCancellationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        try {
            BookingResponseBean response = buildResponse();

            NotificationService.sendBookingCancellation(
                    booking.getStudent().getEmail(),
                    response);

            NotificationService.sendBookingCancellationToTutor(
                    booking.getTutor().getEmail(),
                    response);

        } catch (it.ispwproject.brainbank.exception.NotificationException e) {
            AppLogger.logWarning("Notifica cancellazione non inviata: " + e.getMessage());
        }
    }

    private BookingResponseBean buildResponse() {
        StudentBean studentBean = new StudentBean(
                booking.getStudent().getId(),
                booking.getStudent().getName(),
                booking.getStudent().getSurname(),
                booking.getStudent().getEmail());

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
                booking.getStatus(),
                booking.getMeetLink(),
                studentBean,
                tutorBean,
                subjectBean,
                slotBean);
    }
}