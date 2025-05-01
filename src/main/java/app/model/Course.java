package app.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a course entity in the system.
 */
@Entity
@Table(name = "courses") // Maps to 'courses' table
public class Course {

    // Primary key for the Course
    @Id
    @Column(name = "course_id", nullable = false, unique = true)
    private String courseId;

    // Course name
    @Column(name = "course_name", nullable = false)
    private String courseName;

    // Academic ID (mapped directly as a String rather than as an entity relationship)
    @Column(name = "academic_id", nullable = false)
    private String academicId;

    // A Set of Student IDs associated with the course
    @ElementCollection // Represents a collection of basic types (here, student IDs)
    @CollectionTable(
            name = "course_students", // Join table for mapping student IDs
            joinColumns = @JoinColumn(name = "course_id") // Foreign key linking to 'courses' table
    )
    @Column(name = "student_id") // Column name for student IDs in the join table
    private Set<String> studentIds = new HashSet<>();

    // Constructors
    public Course() {}

    public Course(String courseId, String courseName, String academicId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.academicId = academicId;
        this.studentIds = new HashSet<>();
    }

    // Getter and Setter for courseId
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    // Getter and Setter for course name
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Getter and Setter for academicId
    public String getAcademicId() {
        return academicId;
    }

    public void setAcademicId(String academicId) {
        this.academicId = academicId;
    }

    // Getter and Setter for studentIds
    public Set<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(Set<String> studentIds) {
        this.studentIds = new HashSet<>(studentIds);
    }

    // Methods to add/remove student IDs individually
    public void addStudentId(String studentId) {
        this.studentIds.add(studentId);
    }

    public void removeStudentId(String studentId) {
        this.studentIds.remove(studentId);
    }

    public boolean isStudentEnrolled(String studentId) {
        return this.studentIds.contains(studentId);
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", academicId='" + academicId + '\'' +
                ", studentIds=" + studentIds +
                '}';
    }
}