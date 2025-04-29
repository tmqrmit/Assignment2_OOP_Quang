package app.model;

import app.util.StringSetConverter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "academics")
public class Academic extends Staff {

    @Column(name = "expertise", nullable = false)
    private String expertise;

    @Column(name = "supervised_students", columnDefinition = "TEXT")
    @Convert(converter = StringSetConverter.class)
    private Set<String> supervisedStudents = new HashSet<>();

    public Academic(String personId, String fullName, Date dateOfBirth, String email, String phoneNumber, String staffId, String expertise) {
        super(personId, fullName, dateOfBirth, email, phoneNumber, staffId);
        if (expertise == null || expertise.isEmpty()) {
            throw new IllegalArgumentException("Expertise cannot be null or empty.");
        }
        this.expertise = expertise;
    }

    public Academic() {
        super();
        this.supervisedStudents = new HashSet<>();
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        if (expertise == null || expertise.isEmpty()) {
            throw new IllegalArgumentException("Expertise cannot be null or empty.");
        }
        this.expertise = expertise;
    }

    public Set<String> getSupervisedStudents() {
        return new HashSet<>(supervisedStudents);
    }

    public void superviseStudent(String studentID) {
        supervisedStudents.add(studentID);
    }

    public void stopSupervisingStudent(String studentID) {
        supervisedStudents.remove(studentID);
    }

    public boolean isSupervising(String studentID) {
        return supervisedStudents.contains(studentID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Academic academic = (Academic) o;
        return Objects.equals(getPersonId(), academic.getPersonId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPersonId());
    }

    @Override
    public String toString() {
        return String.format(
                "Academic Staff:\n" +
                        "  Full Name: %s\n" +
                        "  ID: %s\n" +
                        "  Expertise: %s\n" +
                        "  Email: %s\n" +
                        "  Date of Birth: %s\n" +
                        "  Supervised Students: %s\n",
                getFullName(), getPersonId(), expertise, getEmail(), getDateOfBirth(), supervisedStudents
        );
    }
}