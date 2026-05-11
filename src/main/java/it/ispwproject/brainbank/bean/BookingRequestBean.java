package it.ispwproject.brainbank.bean;

public class BookingRequestBean {

    private StudentBean  student;
    private TutorBean    tutor;
    private SubjectBean  subject;
    private TimeSlotBean timeSlot;

    public BookingRequestBean() {}

    public BookingRequestBean(StudentBean student, TutorBean tutor,
                              SubjectBean subject, TimeSlotBean timeSlot) {
        this.student  = student;
        this.tutor    = tutor;
        this.subject  = subject;
        this.timeSlot = timeSlot;
    }

    public StudentBean getStudent() { return student; }
    public void setStudent(StudentBean student) { this.student = student; }
    public TutorBean getTutor() { return tutor; }
    public void setTutor(TutorBean tutor) { this.tutor = tutor; }
    public SubjectBean getSubject() { return subject; }
    public void setSubject(SubjectBean subject) { this.subject = subject; }
    public TimeSlotBean getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlotBean timeSlot) { this.timeSlot = timeSlot; }
}
