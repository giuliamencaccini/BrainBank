package it.ispwproject.brainbank.bean;

import it.ispwproject.brainbank.enumerator.BookingStatus;

public class BookingResponseBean {

    private int id;
    private BookingStatus status;
    private String meetLink;
    private StudentBean student;
    private TutorBean tutor;
    private SubjectBean subject;
    private TimeSlotBean timeSlot;

    public BookingResponseBean() {}

    public BookingResponseBean(int id, BookingStatus status, String meetLink,
                               StudentBean student, TutorBean tutor,
                               SubjectBean subject, TimeSlotBean timeSlot) {
        this.id       = id;
        this.status   = status;
        this.meetLink = meetLink;
        this.student  = student;
        this.tutor    = tutor;
        this.subject  = subject;
        this.timeSlot = timeSlot;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
    public StudentBean getStudent() { return student; }
    public void setStudent(StudentBean student) { this.student = student; }
    public TutorBean getTutor() { return tutor; }
    public void setTutor(TutorBean tutor) { this.tutor = tutor; }
    public SubjectBean getSubject() { return subject; }
    public void setSubject(SubjectBean subject) { this.subject = subject; }
    public TimeSlotBean getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlotBean timeSlot) { this.timeSlot = timeSlot; }
}