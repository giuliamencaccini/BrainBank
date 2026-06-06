package it.ispwproject.brainbank.model;

import java.time.LocalDateTime;

public class Activity {

    private int id;

    private Tutor   tutor;
    private Student student;

    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public Activity() {}

    public Activity(Tutor tutor, Student student, String description) {
        this.tutor       = tutor;
        this.student     = student;
        this.description = description;
        this.completed   = false;
    }

    public int getId() {return id; }
    public void setId(int id) { this.id = id; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public void complete() {
        this.completed = true;
    }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}