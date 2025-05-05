package app.controller.academic;


import app.model.Academic;
import app.model.AppUser;
import app.model.Student;
import app.service.AcademicService;
import app.service.StudentService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AcademicProfileManagementController {

    private Academic academic;
    private AcademicService academicService;

    @FXML
    private DatePicker datePickerDOB;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    public void initialize(AppUser appUser) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("your-persistence-unit");
        this.academicService = new AcademicService(entityManagerFactory.createEntityManager());
        this.academic = academicService.findByPersonId(appUser.getPersonId());

        // Pre-fill current student info
        datePickerDOB.setValue(academic.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        emailField.setText(academic.getEmail());
        phoneField.setText(academic.getPhoneNumber());
    }

    @FXML
    private void handleSaveChanges() {
        String updatedEmail = emailField.getText();
        String updatedPhone = phoneField.getText();
        LocalDate updatedDOB = datePickerDOB.getValue();

        // Basic validation
        if (updatedEmail.isEmpty() || updatedPhone.isEmpty() || updatedDOB == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please complete all fields before saving.");
            alert.showAndWait();
            return;
        }

        // Convert LocalDate to java.util.Date
        Date dob = Date.from(updatedDOB.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Create updated Academic object
        Academic updatedAcademic = new Academic(
                academic.getPersonId(),
                academic.getFullName(),
                dob,
                updatedEmail,
                updatedPhone,
                academic.getStaffId(), academic.getExpertise());

        // Update in backend
        academicService.updateAcademic(updatedAcademic);

        // Show success alert
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Profile Updated");
        alert.setContentText("Your contact information has been updated.");
        alert.showAndWait();
    }
}
