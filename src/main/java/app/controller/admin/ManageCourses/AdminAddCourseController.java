package app.controller.admin.ManageCourses;

import app.model.Course;
import app.service.CourseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;

public class AdminAddCourseController {

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField academicIdField;
    @FXML private TextField studentIdsField;

    private CourseService courseService;

    public void initialize(CourseService courseService) {
        this.courseService = courseService;
    }

    @FXML
    public void handleAddCourse() {
        Course course = new Course();
        course.setCourseId(courseIdField.getText().trim());
        course.setCourseName(courseNameField.getText().trim());
        course.setAcademicId(academicIdField.getText().trim());
        course.setStudentIds(new HashSet<>(Arrays.asList(studentIdsField.getText().split("\\s*,\\s*"))));

        courseService.addCourse(course);

        Stage stage = (Stage) courseIdField.getScene().getWindow();
        stage.close();
    }
}
