package app.controller.admin.ManageAcademics;

import app.model.Academic;
import app.service.AcademicService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class AdminAddAcademicController {

    @FXML private TextField personIdField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField staffIdField;
    @FXML private TextField expertiseField;

    private AcademicService academicService;

    public void initialize(AcademicService academicService) {
        this.academicService = academicService;
    }

    @FXML
    public void addAcademic() {
        Academic academic = new Academic();
        academic.setPersonId(personIdField.getText().trim());
        academic.setFullName(fullNameField.getText().trim());

        if (dateOfBirthPicker.getValue() != null) {
            Date dob = Date.from(dateOfBirthPicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            academic.setDateOfBirth(dob);
        }

        academic.setEmail(emailField.getText().trim());
        academic.setPhoneNumber(phoneNumberField.getText().trim());
        academic.setStaffId(staffIdField.getText().trim());
        academic.setExpertise(expertiseField.getText().trim());

        academicService.saveAcademic(academic);

        Stage stage = (Stage) personIdField.getScene().getWindow();
        stage.close();
    }
}
