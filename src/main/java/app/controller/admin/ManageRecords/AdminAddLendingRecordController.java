package app.controller.admin.ManageRecords;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.LendingService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminAddLendingRecordController {

    private LendingService lendingService;

    @FXML
    private TextField borrowerIdField;

    @FXML
    private TextField responsibleAcademicIdField;

    @FXML
    private TextField equipmentIdsField;

    @FXML
    private TextField purposeField;

    @FXML
    private DatePicker borrowDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    public void initialize(LendingService lendingService) {
        this.lendingService = lendingService;
    }

    @FXML
    private void handleSubmit() {
        String borrowerId = borrowerIdField.getText();
        String academicId = responsibleAcademicIdField.getText();
        String equipmentText = equipmentIdsField.getText();
        Set<String> equipmentSet = Arrays.stream(equipmentText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        String purpose = purposeField.getText();
        String statusText = statusComboBox.getValue().toUpperCase(); // convert to uppercase to match enum
        LendingRecordStatus status = LendingRecordStatus.valueOf(statusText);

        Date borrowDate = Date.from(borrowDatePicker.getValue()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        Date returnDate = Date.from(returnDatePicker.getValue()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (borrowerId.isEmpty() || academicId.isEmpty() || equipmentSet.isEmpty()
                || borrowDatePicker.getValue() == null || returnDatePicker.getValue() == null) {
            showAlert("Please fill in all required fields.");
            return;
        }

        // Use lendingService to save the new record
        LendingRecord newRecord = new LendingRecord(
                null,
                borrowerId,
                equipmentSet,
                academicId,
                borrowDate,
                returnDate,
                status,
                purpose
        );

        newRecord.setApprovalStatus(approvalStatus.APPROVED);

        lendingService.saveRecord(newRecord);

        showAlert("Record added successfully.");
        clearForm();
    }

    @FXML
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearForm() {
        borrowerIdField.clear();
        responsibleAcademicIdField.clear();
        equipmentIdsField.clear();
        purposeField.clear();
        borrowDatePicker.setValue(null);
        returnDatePicker.setValue(null);
        statusComboBox.setValue(null);
    }
}
