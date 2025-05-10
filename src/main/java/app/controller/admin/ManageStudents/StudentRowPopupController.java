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

public class StudentRowPopupController {

    @FXML private TextField personIdField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker dateOfBirthField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;

    private Student student;
    private StudentService studentService;

    public void initialize(StudentService studentService, Student student) {
        this.studentService = studentService;
        this.student = student;

        if (student != null) {
            personIdField.setText(student.getPersonId());
            fullNameField.setText(student.getFullName());
            dateOfBirthField.setValue(student.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            emailField.setText(student.getEmail());
            phoneNumberField.setText(student.getPhoneNumber());
        }
    }

    @FXML
    public void handleSave() {
        student.setPersonId(personIdField.getText().trim());
        student.setFullName(fullNameField.getText().trim());
        student.setDateOfBirth(Date.from(dateOfBirthField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        student.setEmail(emailField.getText().trim());
        student.setPhoneNumber(phoneNumberField.getText().trim());

        if (student.getPersonId() == null) {
            studentService.saveStudent(student);
        } else {
            studentService.updateStudent(student);
        }

        Stage stage = (Stage) personIdField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleDelete() {
        try {
            studentService.removeStudent(student);
            Stage stage = (Stage) personIdField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
