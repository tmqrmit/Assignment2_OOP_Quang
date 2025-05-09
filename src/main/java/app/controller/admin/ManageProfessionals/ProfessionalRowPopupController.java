package app.controller.admin.ManageProfessionals;

import app.model.Academic;
import app.model.Professional;
import app.service.AcademicService;
import app.service.ProfessionalService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Date;

public class ProfessionalRowPopupController {

    @FXML private TextField personIdField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker dateOfBirthField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField staffIdField;
    @FXML private TextField departmentField;

    private Professional professional;
    private ProfessionalService professionalService;

    public void initialize(ProfessionalService professionalService, Professional professional) {
        this.professionalService = professionalService;
        this.professional = professional;

        // Pre-populate fields if an Academic object is provided
        if (professional != null) {
            personIdField.setText(professional.getPersonId());
            fullNameField.setText(professional.getFullName());
            dateOfBirthField.setValue(professional.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            emailField.setText(professional.getEmail());
            phoneNumberField.setText(professional.getPhoneNumber());
            staffIdField.setText(professional.getStaffId());
            departmentField.setText(professional.getDepartment());
        }
    }

    @FXML
    public void handleSave() {
        professional.setPersonId(personIdField.getText().trim());
        professional.setFullName(fullNameField.getText().trim());
        professional.setDateOfBirth(Date.from(dateOfBirthField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        professional.setEmail(emailField.getText().trim());
        professional.setPhoneNumber(phoneNumberField.getText().trim());
        professional.setStaffId(staffIdField.getText().trim());
        professional.setDepartment(departmentField.getText().trim());

        if (professional.getPersonId() == null) {
            // Create a new Academic if it doesn't exist
            professionalService.saveProfessional(professional);
        } else {
            // Update the existing Academic
            professionalService.updateProfessional(professional);
        }

        Stage stage = (Stage) personIdField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleDelete() {
        try {
            professionalService.removeProfessional(professional);
            Stage stage = (Stage) personIdField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
