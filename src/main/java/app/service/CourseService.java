package app.service;

import app.model.Course;

import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Service layer for managing course operations.
 */
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
    public void saveCourse(Course course) {
        Course existingCourse = findByCourseId(course.getCourseId());
        if (existingCourse != null) {
            throw new IllegalArgumentException("A course with ID " + course.getCourseId() + " already exists.");
        }
        entityManager.persist(course);
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

        entityManager.merge(course);
        return existingCourse;
    }

    /**
     * Delete a Course entity by its `courseId`.
     *
     * @param courseId the ID of the Course to delete
     */
    public void deleteCourseById(String courseId) {
        Course course = findByCourseId(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " does not exist.");
        }
        entityManager.remove(course);
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