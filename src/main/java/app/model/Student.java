package app.model;

import app.util.StringSetConverter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "student")
public class Student extends Person {

    private String studentId;

    @Column(name = "lending_records", columnDefinition = "TEXT")
    @Convert(converter = StringSetConverter.class)
    private Set<String> lendingRecords = new HashSet<>();

    public Student() {}

    public Student(String personId, String fullName, Date dateOfBirth, String email, String phoneNumber, String studentId) {
        super(personId, fullName, dateOfBirth, email, phoneNumber);
        if (studentId == null || studentId.isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty.");
        }
        this.studentId = studentId;
        this.lendingRecords = new HashSet<>();
    }

    public String getStudentId() {
        return this.studentId;
    }

    public Set<String> getLendingRecords() {
        return new HashSet<>(this.lendingRecords);
    }

    public void addLendingRecord(String lendingRecordID) {
        this.lendingRecords.add(lendingRecordID);
    }

    public void removeLendingRecord(String lendingRecordID) {
        this.lendingRecords.remove(lendingRecordID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return Objects.equals(this.studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }
}