package it.ispwproject.brainbank.bean;

import it.ispwproject.brainbank.bean.TutorBean;

import java.time.LocalDateTime;

public class ActivityBean {

    private int id;
    private StudentBean student;
    private TutorBean tutor;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public ActivityBean() {}

    public ActivityBean(int id, StudentBean student, TutorBean tutor, String description,
                        boolean completed, LocalDateTime createdAt) {
        this.id          = id;
        this.student     = student;
        this.tutor       = tutor;
        this.description = description;
        this.completed   = completed;
        this.createdAt   = createdAt;
    }

    // costruttore legacy senza tutor per retrocompatibilità
    public ActivityBean(int id, StudentBean student, String description,
                        boolean completed, LocalDateTime createdAt) {
        this(id, student, null, description, completed, createdAt);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public StudentBean getStudent() { return student; }
    public void setStudent(StudentBean student) { this.student = student; }
    public TutorBean getTutor() { return tutor; }
    public void setTutor(TutorBean tutor) { this.tutor = tutor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}