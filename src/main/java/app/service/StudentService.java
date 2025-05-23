package app.service;

import app.model.LendingRecord;
import app.model.Student;
import app.model.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
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
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            if (findByPersonId(student.getPersonId()) != null) {
                throw new IllegalArgumentException("Student with ID " + student.getPersonId() + " already exists.");
            }
            transaction.begin();
            entityManager.persist(student);
            System.out.println("Student updated successfully");
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
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
     *
     *
     * @param student the Student to update
     * @return void
     *
     */
    public void updateStudent(Student student) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(student);
            System.out.println("Student updated successfully");
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Remove a Student entity by studentId.
     *
     * @param student to delete
     */
    public void removeStudent(Student student) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(student);
            System.out.println("Student removed successfully");
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
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

}