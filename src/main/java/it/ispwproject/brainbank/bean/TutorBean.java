package it.ispwproject.brainbank.bean;

public class TutorBean {
    private int id;
    private String name;
    private String surname;
    private String bio;
    private String email;
    private boolean favourite;

    public TutorBean() {}

    public TutorBean(int id, String name, String surname, String bio, String email, boolean favourite) {
        this.id       = id;
        this.name     = name;
        this.surname  = surname;
        this.bio      = bio;
        this.email    = email;
        this.favourite = favourite;
    }

    public String getFullName() { return name + " " + surname; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isFavourite() { return favourite; }
    public void setFavourite(boolean favourite) { this.favourite = favourite; }
}