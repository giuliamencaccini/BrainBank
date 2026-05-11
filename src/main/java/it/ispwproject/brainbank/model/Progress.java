package it.ispwproject.brainbank.model;

import java.time.LocalDateTime;

public class Progress {

    /**
     * id — IDENTIFICATORE PROPRIO di questo record di progresso.
     * NON è una FK — è l'identità di questo oggetto.
     */
    private int id;

    /**
     * Puntatori diretti agli oggetti correlati.
     * NON sono FK (tutorId, studentId) — sono riferimenti in memoria.
     */
    private Tutor   tutor;
    private Student student;

    private String notes;
    private LocalDateTime updatedAt;

    public Progress() {}

    public Progress(Tutor tutor, Student student, String notes) {
        this.tutor   = tutor;
        this.student = student;
        this.notes   = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}