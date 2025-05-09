package app.controller.admin.ManageProfessionals;

import app.model.Professional;
import app.service.ProfessionalService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Date;

public class AdminAddProfessionalController {

    @FXML private TextField personIdField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField staffIdField;
    @FXML private TextField departmentField;

    private ProfessionalService professionalService;

    public void initialize(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @FXML
    public void addProfessional() {
        Professional professional = new Professional();
        professional.setPersonId(personIdField.getText().trim());
        professional.setFullName(fullNameField.getText().trim());

        if (dateOfBirthPicker.getValue() != null) {
            Date dob = Date.from(dateOfBirthPicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            professional.setDateOfBirth(dob);
        }

        professional.setEmail(emailField.getText().trim());
        professional.setPhoneNumber(phoneNumberField.getText().trim());
        professional.setStaffId(staffIdField.getText().trim());
        professional.setDepartment(departmentField.getText().trim());

        professionalService.saveProfessional(professional);

        Stage stage = (Stage) personIdField.getScene().getWindow();
        stage.close();
    }
}
