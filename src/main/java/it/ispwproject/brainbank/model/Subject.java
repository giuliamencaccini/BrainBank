package it.ispwproject.brainbank.model;

public class Subject {

    /**
     * id — IDENTIFICATORE PROPRIO dell'entità Subject.
     * Serve al DAO per identificare la materia nelle query SQL.
     * NON è una FK — è l'identità di questo oggetto.
     */
    private int id;

    private String name;

    public Subject() {}

    public Subject(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    @Override
    public String toString() { return name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}