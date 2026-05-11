package it.ispwproject.brainbank.bean;

import java.time.LocalDateTime;

public class ActivityBean {

    private int id;
    private StudentBean student;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public ActivityBean() {}

    public ActivityBean(int id, StudentBean student, String description,
                        boolean completed, LocalDateTime createdAt) {
        this.id          = id;
        this.student     = student;
        this.description = description;
        this.completed   = completed;
        this.createdAt   = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public StudentBean getStudent() { return student; }
    public void setStudent(StudentBean student) { this.student = student; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}