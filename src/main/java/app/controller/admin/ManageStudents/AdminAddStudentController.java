package app.controller.admin.ManageStudents;

import app.model.Academic;
import app.model.Student;
import app.service.AcademicService;
import app.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Date;

public class AdminAddStudentController {

    @FXML private TextField personIdField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;

    private StudentService studentService;

    public void initialize(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void addStudent() {
        Student student = new Student();
        student.setPersonId(personIdField.getText().trim());
        student.setFullName(fullNameField.getText().trim());

        if (dateOfBirthPicker.getValue() != null) {
            Date dob = Date.from(dateOfBirthPicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            student.setDateOfBirth(dob);
        }

        student.setEmail(emailField.getText().trim());
        student.setPhoneNumber(phoneNumberField.getText().trim());

        studentService.saveStudent(student);

        Stage stage = (Stage) personIdField.getScene().getWindow();
        stage.close();
    }
}
