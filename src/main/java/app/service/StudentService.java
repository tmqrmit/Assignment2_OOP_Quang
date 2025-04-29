package app.service;

import app.model.LendingRecord;
import app.model.Student;
import app.model.Course;

import jakarta.persistence.EntityManager;

import java.util.List;

public class StudentService {

    private final EntityManager entityManager;

    /**
     * Constructor for StudentService.
     * Injects an EntityManager directly into the service.
     *
     * @param entityManager the EntityManager used for student management
     */
    public StudentService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Save a new Student entity.
     *
     * Ensures that no duplicate studentId exists before saving.
     *
     * @param student the Student to be saved
     * @throws IllegalArgumentException if a Student with the same studentId already exists
     */
    public void saveStudent(Student student) {
        // Check for duplicate studentId
        Student existingStudent = findByPersonId(student.getStudentId());
        if (existingStudent != null) {
            throw new IllegalArgumentException("Student with studentId " + student.getStudentId() + " already exists.");
        }
        entityManager.persist(student);
    }

    /**
     * Find a Student entity by its studentId.
     *
     * @param personId the studentId of the Student to retrieve
     * @return the Student, or null if not found
     */
    public Student findByPersonId(String personId) {
        try {
            return entityManager.createQuery(
                    "SELECT s FROM Student s WHERE s.personId = :personId", Student.class
            ).setParameter("personId", personId).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // Return null when no result is found
        }
    }

    /**
     * Retrieve all Student entities.
     *
     * @return a list of all Students
     */
    public List<Student> findAllStudents() {
        return entityManager.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    /**
     * Update an existing Student entity.
     *
     * Ensures updated studentId does not conflict with existing records.
     *
     * @param student the Student to update
     * @return the updated Student entity
     * @throws IllegalArgumentException if the updated studentId already exists in another record
     */
    public Student updateStudent(Student student) {
        // Check for duplicate studentId with a different Student
        Student existingStudent = findByPersonId(student.getStudentId());
        if (existingStudent != null && !existingStudent.getPersonId().equals(student.getPersonId())) {
            throw new IllegalArgumentException("Student with studentId " + student.getStudentId() + " already exists.");
        }
        return entityManager.merge(student);
    }

    /**
     * Remove a Student entity by studentId.
     *
     * @param studentId the studentId of the Student to delete
     */
    public void deleteStudentByStudentId(String studentId) {
        Student student = findByPersonId(studentId);
        if (student != null) {
            entityManager.remove(student);
        }
    }

    /**
     * Process a lending request for a Student.
     *
     * Performs validations to ensure the lending request can proceed.
     *
     * @param studentId       the studentId of the borrower
     * @param lendingRecord   the LendingRecord object
     * @param courseToBorrowFor the Course object the student is borrowing for
     * @throws IllegalArgumentException if validations fail
     */
    public void processLendingRequest(String studentId, LendingRecord lendingRecord, Course courseToBorrowFor) {
        Student student = findByPersonId(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " does not exist.");
        }

        if (lendingRecord == null) {
            throw new IllegalArgumentException("Lending record cannot be null.");
        }

        if (lendingRecord.getResponsibleAcademic() == null) {
            throw new IllegalArgumentException("Responsible academic for lending record cannot be null.");
        }

        if (!courseToBorrowFor.isStudentEnrolled(student.getStudentId())) {
            throw new IllegalArgumentException("Student with ID " + student.getStudentId() + " is not enrolled in the course.");
        }

        if (!courseToBorrowFor.getAcademicId().equals(lendingRecord.getResponsibleAcademic())) {
            throw new IllegalArgumentException("Lending must be approved by the academic responsible for the course.");
        }

        student.addLendingRecord(lendingRecord.getRecordId());
        saveStudent(student); // Update student with new lending record
    }

    /**
     * Retrieve all lending records of a Student by studentId.
     *
     * @param studentId the studentId of the Student
     * @return a list of lending record IDs
     */
    public List<String> getLendingRecords(String studentId) {
        Student student = findByPersonId(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " does not exist.");
        }
        return List.copyOf(student.getLendingRecords());
    }

    /**
     * Remove a lending record from a student's record by studentId.
     *
     * @param studentId       the studentId of the Student
     * @param lendingRecordId the ID of the lending record to remove
     * @throws IllegalArgumentException if Student or lending record is not found
     */
    public void removeLendingRecord(String studentId, String lendingRecordId) {
        Student student = findByPersonId(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " does not exist.");
        }
        student.removeLendingRecord(lendingRecordId);
        saveStudent(student); // Update student
    }
}