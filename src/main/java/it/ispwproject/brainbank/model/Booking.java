package it.ispwproject.brainbank.model;

import it.ispwproject.brainbank.enumerator.BookingStatus;
import it.ispwproject.brainbank.pattern.observer.Observable;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Booking extends Observable {

    private int id;

    private Student  student;
    private Tutor    tutor;
    private Subject  subject;
    private TimeSlot timeSlot;

    private BookingStatus status;
    private String meetLink;
    private LocalDateTime createdAt;

    public Booking() {}

    public Booking(Student student, Tutor tutor,
                   Subject subject, TimeSlot timeSlot) {
        this.student   = student;
        this.tutor     = tutor;
        this.subject   = subject;
        this.timeSlot  = timeSlot;
        this.status    = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now(ZoneId.systemDefault());
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        notifyObservers();
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        notifyObservers();
    }

    public boolean isExpired() {
        return this.status == BookingStatus.EXPIRED;
    }

    public boolean belongsTo(Student s) {
        return this.student != null && this.student.getId() == s.getId();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}