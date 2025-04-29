package app.model;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Represents a professional staff member in a university.
 * This class extends Staff and includes additional information such as the department.
 */
@Entity
@Table(name = "professional_staff")
public class Professional extends Staff {

    private String academicId;

    @Column(name = "department", nullable = false)
    private String department;

    // Default constructor for JPA
    public Professional() {
        super();
    }

    /**
     * Constructs a Professional staff member.
     *
     * @param personId   The unique ID of the person.
     * @param fullName   The full name of the staff member.
     * @param dateOfBirth The birth date of the staff member.
     * @param email      The email address of the staff member.
     * @param phoneNumber The phone number of the staff member.
     * @param staffId    The unique staff ID.
     * @param department The department where the staff member works.
     */
    public Professional(String personId, String academicId, String fullName, Date dateOfBirth,
                        String email, String phoneNumber, String staffId, String department) {
        super(personId, fullName, dateOfBirth, email, phoneNumber, staffId);
        this.academicId = academicId;
        this.department = department;
    }

    /**
     * Gets the department of the professional staff member.
     *
     * @return The department name.
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the department of the professional staff member.
     *
     * @param department The new department name.
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Returns a string representation of the Professional object.
     *
     * @return A formatted string with professional staff details.
     */
    @Override
    public String toString() {
        return String.format(
                "Professional Staff:\n" +
                        "  Full Name: %s\n" +
                        "  ID: %s\n" +
                        "  Department: %s\n" +
                        "  Email: %s\n" +
                        "  Date of Birth: %s\n",
                getFullName(), getStaffId(), department, getEmail(), getDateOfBirth()
        );
    }
}