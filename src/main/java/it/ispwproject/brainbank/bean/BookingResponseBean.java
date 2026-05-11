package it.ispwproject.brainbank.bean;

public class BookingResponseBean {

    private int id;
    private String status;
    private String meetLink;
    private TutorBean tutor;
    private SubjectBean subject;
    private TimeSlotBean timeSlot;

    public BookingResponseBean() {}

    public BookingResponseBean(int id, String status, String meetLink,
                               TutorBean tutor, SubjectBean subject,
                               TimeSlotBean timeSlot) {
        this.id       = id;
        this.status   = status;
        this.meetLink = meetLink;
        this.tutor    = tutor;
        this.subject  = subject;
        this.timeSlot = timeSlot;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
    public TutorBean getTutor() { return tutor; }
    public void setTutor(TutorBean tutor) { this.tutor = tutor; }
    public SubjectBean getSubject() { return subject; }
    public void setSubject(SubjectBean subject) { this.subject = subject; }
    public TimeSlotBean getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlotBean timeSlot) { this.timeSlot = timeSlot; }
}
