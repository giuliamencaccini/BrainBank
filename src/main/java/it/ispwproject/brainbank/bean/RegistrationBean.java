package it.ispwproject.brainbank.bean;

import it.ispwproject.brainbank.enumerator.Role;

import java.util.List;

public class RegistrationBean {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;

    // Solo per tutor
    private String bio;

    /**
     * subjects — lista di SubjectBean invece di List<Integer>.
     * Coerente con il pattern OO — oggetti, non ID.
     */
    private List<SubjectBean> subjects;

    public RegistrationBean() {
        // Fields are populated step by step during CLI registration
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public List<SubjectBean> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectBean> subjects) { this.subjects = subjects; }
}
