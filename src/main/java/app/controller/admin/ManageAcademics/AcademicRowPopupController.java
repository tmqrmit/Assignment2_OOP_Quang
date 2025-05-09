package app.controller.admin.ManageAcademics;

import app.model.Academic;
import app.service.AcademicService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class AcademicRowPopupController {

    @FXML private TextField personIdField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker dateOfBirthField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField staffIdField;
    @FXML private TextField expertiseField;
    @FXML private TextField supervisedStudentsField;

    private Academic academic;
    private AcademicService academicService;

    public void initialize(AcademicService academicService, Academic academic) {
        this.academicService = academicService;
        this.academic = academic;

        // Pre-populate fields if an Academic object is provided
        if (academic != null) {
            personIdField.setText(academic.getPersonId());
            fullNameField.setText(academic.getFullName());
            dateOfBirthField.setValue(academic.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            emailField.setText(academic.getEmail());
            phoneNumberField.setText(academic.getPhoneNumber());
            staffIdField.setText(academic.getStaffId());
            expertiseField.setText(academic.getExpertise());
            supervisedStudentsField.setText(String.join(", ", academic.getSupervisedStudents()));
        }
    }

    @FXML
    public void handleSave() {
        academic.setPersonId(personIdField.getText().trim());
        academic.setFullName(fullNameField.getText().trim());
        academic.setDateOfBirth(Date.from(dateOfBirthField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        academic.setEmail(emailField.getText().trim());
        academic.setPhoneNumber(phoneNumberField.getText().trim());
        academic.setStaffId(staffIdField.getText().trim());
        academic.setExpertise(expertiseField.getText().trim());

        if (academic.getPersonId() == null) {
            // Create a new Academic if it doesn't exist
            academicService.saveAcademic(academic);
        } else {
            // Update the existing Academic
            academicService.updateAcademic(academic);
        }

        Stage stage = (Stage) personIdField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleDelete() {
        try {
            academicService.removeAcademic(academic);
            Stage stage = (Stage) personIdField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
