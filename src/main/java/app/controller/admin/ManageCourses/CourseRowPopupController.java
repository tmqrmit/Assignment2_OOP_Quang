package app.controller.admin.ManageCourses;

import app.model.Course;
import app.service.CourseService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;

public class CourseRowPopupController {

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField academicIdField;
    @FXML private TextField studentIdsField;

    private Course course;
    private CourseService courseService;

    public void initialize(CourseService courseService, Course course) {
        this.courseService = courseService;
        this.course = course;
        courseIdField.setText(course.getCourseId());
        courseNameField.setText(course.getCourseName());
        academicIdField.setText(course.getAcademicId());
        studentIdsField.setText(String.join(", ", course.getStudentIds()));
    }

    @FXML
    public void handleSave() {
        course.setCourseId(courseIdField.getText().trim());
        course.setCourseName(courseNameField.getText().trim());
        course.setAcademicId(academicIdField.getText().trim());
        course.setStudentIds(
                new HashSet<>(Arrays.asList(studentIdsField.getText().split("\\s*,\\s*")))
        );

        courseService.updateCourse(course);

        Stage stage = (Stage) courseIdField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleDelete() {
        try {
            courseService.deleteCourse(course);
            Stage stage = (Stage) courseIdField.getScene().getWindow();
            stage.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
