package app.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Base entity class for all person entities.
 * It defines personId as the primary key, which will be inherited
 * and used by subclasses.
 */
@MappedSuperclass
public abstract class Person {

    @Id
    @Column(name = "person_id", nullable = false, unique = true)
    protected String personId;

    @Column(name = "full_name", nullable = false)
    protected String fullName;

    @Temporal(TemporalType.DATE) // Stores only the DATE portion
    @Column(name = "date_of_birth", nullable = false)
    protected Date dateOfBirth;

    @Column(name = "email", unique = true, nullable = false)
    protected String email;

    @Column(name = "phone_number", nullable = false)
    protected String phoneNumber;

    /**
     * Default constructor (required by JPA).
     */
    public Person() {}

    /**
     * Constructor with parameters.
     *
     * @param personId The primary key of a person.
     * @param fullName The full name of the person.
     * @param dateOfBirth The date of birth of the person.
     * @param email The email of the person.
     * @param phoneNumber The phone number of the person.
     */
    public Person(String personId, String fullName, Date dateOfBirth, String email, String phoneNumber) {
        this.personId = personId;
        this.fullName = fullName;
        this.dateOfBirth = new Date(dateOfBirth.getTime());
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDateOfBirth() {
        return new Date(dateOfBirth.getTime());
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = new Date(dateOfBirth.getTime());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // equals, hashCode, toString

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person)) return false;
        Person other = (Person) obj;
        return Objects.equals(personId, other.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId);
    }

    @Override
    public String toString() {
        return String.format(
                "Person[ID=%s, Name=%s, DOB=%tF, Email=%s, Phone=%s]",
                personId, fullName, dateOfBirth, email, phoneNumber
        );
    }
}