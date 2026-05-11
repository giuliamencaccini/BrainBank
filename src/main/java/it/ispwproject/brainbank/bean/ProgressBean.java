package it.ispwproject.brainbank.bean;

import java.time.LocalDateTime;

public class ProgressBean {

    private StudentBean student;
    private String notes;
    private LocalDateTime updatedAt;

    public ProgressBean() {}

    public ProgressBean(StudentBean student, String notes, LocalDateTime updatedAt) {
        this.student   = student;
        this.notes     = notes;
        this.updatedAt = updatedAt;
    }

    public StudentBean getStudent() { return student; }
    public void setStudent(StudentBean student) { this.student = student; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}