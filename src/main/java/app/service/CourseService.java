package app.service;

import app.model.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service layer for managing course operations.
 */
@Transactional
public class CourseService {

    private final EntityManager entityManager;

    /**
     * Constructor for CourseService.
     * Injects an EntityManager directly into the service.
     *
     * @param entityManager the EntityManager for persistence operations
     */
    public CourseService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Save a new Course entity.
     *
     * Ensures a unique `courseId` exists before saving.
     *
     * @param course the Course to be saved
     * @throws IllegalArgumentException if a Course with the same `courseId` already exists
     */
    public void addCourse(Course course) {
        Course existingCourse = findByCourseId(course.getCourseId());
        if (existingCourse != null) {
            throw new IllegalArgumentException("A course with ID " + course.getCourseId() + " already exists.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(course);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Find a Course entity by its `courseId` attribute.
     *
     * @param courseId the courseId to search for
     * @return the matching Course, or null if not found
     */
    public Course findByCourseId(String courseId) {
        return entityManager.createQuery("SELECT c FROM Course c WHERE c.courseId = :courseId", Course.class)
                .setParameter("courseId", courseId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Find course by academicId
     */
    public long getCourseCountByAcademicId(String academicId) {
        return entityManager.createQuery("SELECT COUNT(c) FROM Course c WHERE c.academicId = :academicId", Long.class)
                .setParameter("academicId", academicId)
                .getSingleResult();
    }
    /**
     * Retrieve all Course entities.
     *
     * @return a list of all Courses
     */
    public List<Course> findAllCourses() {
        return entityManager.createQuery("SELECT c FROM Course c", Course.class).getResultList();
    }

    /**
     * Update an existing Course entity.
     *
     * Ensures that updated properties do not conflict with existing records.
     *
     * @param course the Course to update
     * @return the updated Course entity
     * @throws IllegalArgumentException if the updated properties conflict with existing data
     */
    public Course updateCourse(Course course) {
        Course existingCourse = findByCourseId(course.getCourseId());
        if (existingCourse == null) {
            throw new IllegalArgumentException("Course with ID " + course.getCourseId() + " does not exist.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(course);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }

        return existingCourse;
    }

    /**
     * Delete a Course .
     *
     * @param course to delete
     */
    public void deleteCourse(Course course) {
        boolean found = findByCourseId(course.getCourseId()) != null;
        if (!found) {
            throw new IllegalArgumentException("Course does not exist.");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.remove(course);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Enroll a student in a course by adding the student ID to the Course.
     *
     * @param courseId  the ID of the Course
     * @param studentId the ID of the Student to enroll
     * @throws IllegalArgumentException if the Course does not exist or the Student is already enrolled
     */
    public void enrollStudent(String courseId, String studentId) {
        Course course = findByCourseId(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " does not exist.");
        }
        if (course.isStudentEnrolled(studentId)) {
            throw new IllegalArgumentException("Student with ID " + studentId + " is already enrolled in the course.");
        }
        course.addStudentId(studentId);
        entityManager.merge(course);
    }

    /**
     * Unenroll a student from a course by removing the student ID from the Course.
     *
     * @param courseId  the ID of the Course
     * @param studentId the ID of the Student to unenroll
     * @throws IllegalArgumentException if the Course does not exist or the Student is not enrolled
     */
    public void unenrollStudent(String courseId, String studentId) {
        Course course = findByCourseId(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " does not exist.");
        }
        if (!course.isStudentEnrolled(studentId)) {
            throw new IllegalArgumentException("Student with ID " + studentId + " is not enrolled in the course.");
        }
        course.removeStudentId(studentId);
        entityManager.merge(course);
    }

    /**
     * Check if there are any courses that relate a student to an academic.
     *
     * @param studentId  the personId of the student
     * @param academicId the personId of the academic
     * @return true if there is at least one course that relates the student to the academic, false otherwise
     */
    public boolean hasCourseRelatingStudentToAcademic(String studentId, String academicId) {
        // Retrieve all courses from the database
        List<Course> allCourses = findAllCourses();

        // Check if any course has the given academic and includes the student in its enrollment
        return allCourses.stream()
                .anyMatch(course -> course.getAcademicId().equals(academicId) && course.isStudentEnrolled(studentId));
    }

}