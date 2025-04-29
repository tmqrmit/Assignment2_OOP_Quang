package app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import java.util.Date;

/**
 * The Staff class extends Person and adds staff-specific fields.
 * It is marked as a MappedSuperclass and does not create its own table.
 */
@MappedSuperclass
public abstract class Staff extends Person {

    @Column(name = "staff_id")
    private String staffId;

    // Default constructor
    public Staff() {}

    public Staff(String personId, String fullName, Date dateOfBirth, String email, String phoneNumber, String staffId) {
        super(personId, fullName, dateOfBirth, email, phoneNumber);
        this.staffId = staffId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}