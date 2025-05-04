package app.controller.academic;

import app.model.AppUser;
import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.AcademicService;
import app.service.CourseService;
import app.service.InventoryService;
import app.service.LendingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class AcademicBorrowEquipmentController {

    private String academicId;
    private EntityManager entityManager;
    private InventoryService inventoryService;
    private AcademicService academicService;
    private CourseService courseService;
    private LendingService lendingService;

    @FXML
    private TextField equipmentIdsField;

    @FXML
    private DatePicker borrowDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private TextField purposeField;

    @FXML
    public void initialize(AppUser appUser) {
        // Initialize the EntityManager
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();

        // Initialize services
        InventoryService inventoryService = new InventoryService(entityManager);
        AcademicService academicService = new AcademicService(entityManager);
        CourseService courseService = new CourseService(entityManager);
        LendingService lendingService = new LendingService(entityManager, inventoryService, null, academicService, courseService);

        this.academicId = appUser.getPersonId(); // Assuming academic's personId is their ID
        this.entityManager = entityManager;
        this.inventoryService = inventoryService;
        this.academicService = academicService;
        this.courseService = courseService;
        this.lendingService = lendingService;
    }

    @FXML
    private void handleSubmit() {
        try {
            String equipmentIdsInput = equipmentIdsField.getText().trim();
            LocalDate borrowDate = borrowDatePicker.getValue();
            LocalDate returnDate = returnDatePicker.getValue();
            String purpose = purposeField.getText().trim();

            if (equipmentIdsInput.isEmpty() || borrowDate == null || returnDate == null || purpose.isEmpty()) {
                showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
                return;
            }

            // Parse Equipment IDs
            Set<String> equipmentIds = new HashSet<>();
            for (String id : equipmentIdsInput.split(",")) {
                equipmentIds.add(id.trim());
            }

            try {
                lendingService.validateEquipment(equipmentIds);
            }
            catch (Exception e) {
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
                return;
            }

            LendingRecord lendingRecord = new LendingRecord(
                    null,
                    academicId,
                    equipmentIds,
                    null,
                    java.sql.Date.valueOf(borrowDate),
                    java.sql.Date.valueOf(returnDate),
                    LendingRecordStatus.BORROWED,
                    purpose
            );
            // For academic, it's always approved
            lendingRecord.setApprovalStatus(approvalStatus.APPROVED);

            // Set all equipment to BORROWED
            lendingService.setAllEquipmentToBorrowed(lendingRecord.getBorrowedEquipment());

            // Save the record
            lendingService.saveRecord(lendingRecord);

            closeForm();
            showAlert("Success", "Lending request submitted successfully!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit the lending request. Please try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) equipmentIdsField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
