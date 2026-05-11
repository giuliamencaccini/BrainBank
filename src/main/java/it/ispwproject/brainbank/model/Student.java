package it.ispwproject.brainbank.model;

import it.ispwproject.brainbank.enumerator.Role;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    /**
     * favouriteTutors — PUNTATORI a oggetti Tutor.
     * NON è una lista di ID — sono riferimenti diretti in memoria.
     */
    private List<Tutor> favouriteTutors;

    public Student() {
        super();
        this.favouriteTutors = new ArrayList<>();
    }

    public Student(int id, String name, String surname,
                   String email, String password) {
        super(id, name, surname, email, password, Role.STUDENT);
        this.favouriteTutors = new ArrayList<>();
    }

    public boolean hasFavourite(int tutorId) {
        return favouriteTutors.stream()
                .anyMatch(t -> t.getId() == tutorId);
    }

    public void addFavourite(Tutor tutor) {
        if (!hasFavourite(tutor.getId())) {
            favouriteTutors.add(tutor);
        }
    }

    public void removeFavourite(int tutorId) {
        favouriteTutors.removeIf(t -> t.getId() == tutorId);
    }

    public List<Tutor> getFavouriteTutors() { return favouriteTutors; }
    public void setFavouriteTutors(List<Tutor> tutors) { this.favouriteTutors = tutors; }
}