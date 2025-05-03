package app;

import app.model.Course;
import app.model.LendingRecord;
import app.model.Student;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.InventoryService;
import app.service.LendingService;
import app.service.StudentService;
import app.service.AcademicService;
import app.service.CourseService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Test {
//    public static void main(String[] args) {
//        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
//        CourseService courseService = new CourseService(entityManager);
//        Course course = courseService.findByCourseId("C001");
//        StudentService studentService = new StudentService(entityManager);
//        Student student = studentService.findByPersonId("P001");
//        System.out.println(student.getFullName());
//
//        System.out.println(course.getStudentIds());
//        System.out.println(course.isStudentEnrolled(student.getPersonId()));
//    }
}