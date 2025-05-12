package app.controller.admin.ManageRecords;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.LendingService;
import app.service.StudentService;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class LendingRecordRowPopupController {

    @FXML
    private TextField borrowerIdField;

    @FXML
    private TextField responsibleAcademicIdField;

    @FXML
    private TextField equipmentIdField;

    @FXML
    private DatePicker borrowDateField;

    @FXML
    private DatePicker returnDateField;

    @FXML
    private ComboBox<LendingRecordStatus> statusBox;

    @FXML
    private TextField purposeField;

    private StudentService studentService;
    private LendingService lendingService;
    private LendingRecord record;

    @FXML
    public void setData(LendingService lendingService, LendingRecord record) {
        this.studentService = new StudentService(Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager());
        this.lendingService = lendingService;
        this.record = record;

        // Set up combo box
        statusBox.getItems().setAll(LendingRecordStatus.values());

        // Populate fields with current data
        borrowerIdField.setText(record.getBorrower());
        responsibleAcademicIdField.setText(record.getResponsibleAcademic());
        equipmentIdField.setText(String.join(", ", record.getBorrowedEquipment().stream()
                .map(Object::toString)
                .toArray(String[]::new)));
        // Define the date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Convert to string and set value
        borrowDateField.setValue(LocalDate.parse(sdf.format(record.getBorrowDate())));
        returnDateField.setValue(LocalDate.parse(sdf.format(record.getReturnDate())));
        statusBox.setValue(record.getStatus());
        purposeField.setText(record.getPurpose());
    }

    @FXML
    private void handleUpdate() {
        try {
            LendingRecordStatus prevStatus = record.getStatus();
            approvalStatus prevApprovalStatus = record.getApprovalStatus();

            String updatedBorrower = borrowerIdField.getText();
            String updatedResponsibleAcademic = responsibleAcademicIdField.getText();
            String updatedEquipment = equipmentIdField.getText();
            Set<String> equipmentSet = Arrays.stream(updatedEquipment.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            Date updatedBorrowDate = Date.from(borrowDateField.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());

            Date updatedReturnDate = Date.from(returnDateField.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
            LendingRecordStatus updatedStatus = statusBox.getValue();
            String updatedPurpose = purposeField.getText();

            // Check if borrower is a student
            boolean isStudent = (studentService.findByPersonId(updatedBorrower) != null);

            // Handle if returning the record
            if (!prevStatus.equals(LendingRecordStatus.RETURNED) && updatedStatus.equals(LendingRecordStatus.RETURNED)) {
                lendingService.returnLendingRecord(record);
            }
            LendingRecord updated = new LendingRecord(record.getRecordId(), updatedBorrower, equipmentSet, updatedResponsibleAcademic, updatedBorrowDate, updatedReturnDate, updatedStatus, updatedPurpose);

            // Set approval status to previous one
            updated.setApprovalStatus(prevApprovalStatus);

            lendingService.updateLendingRecord(updated);

            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error updating record.");
        }
    }

    @FXML
    private void handleDelete() {
        try {
            lendingService.removeLendingRecord(record);
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error deleting record.");
        }
    }

    @FXML
    private void closeWindow() {
        ((Stage) borrowerIdField.getScene().getWindow()).close();
    }

    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
