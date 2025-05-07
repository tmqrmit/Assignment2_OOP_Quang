package app.controller.admin.ManageRecords;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class AdminManageRecordsController {

    @FXML private TableView<LendingRecord> recordsTableView;

    @FXML private TableColumn<LendingRecord, String> borrowerIdColumn;
    @FXML private TableColumn<LendingRecord, String> responsibleAcademicIdColumn;
    @FXML private TableColumn<LendingRecord, String> borrowDateColumn;
    @FXML private TableColumn<LendingRecord, String> returnDateColumn;
    @FXML private TableColumn<LendingRecord, String> equipmentColumn;
    @FXML private TableColumn<LendingRecord, String> purposeColumn;
    @FXML private TableColumn<LendingRecord, String> statusColumn;
    @FXML private TableColumn<LendingRecord, String> approvalColumn;

    @FXML private TextField borrowerIdField;
    @FXML private TextField responsibleAcademicIdField;
    @FXML private DatePicker borrowStartPicker;
    @FXML private DatePicker borrowEndPicker;
    @FXML private DatePicker returnStartPicker;
    @FXML private DatePicker returnEndPicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> approvalComboBox;

    private LendingService lendingService;

    /**
     * Initializes the controller with required services and dependencies.
     */
    @FXML
    public void initialize(LendingService lendingService) {
        this.lendingService = lendingService;
        loadLendingRecords();
        setupTableColumns();
        setupTableRows();
    }

    /**
     * Configures the columns of the lending record table.
     */
    @FXML
    private void setupTableColumns() {
        borrowerIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBorrower()));
        responsibleAcademicIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResponsibleAcademic()));
        borrowDateColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBorrowDate())));
        returnDateColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getReturnDate())));
        equipmentColumn.setCellValueFactory(data -> {
            Set<String> equipmentSet = data.getValue().getBorrowedEquipment();
            String joined = (equipmentSet != null) ? String.join(", ", equipmentSet) : "";
            return new SimpleStringProperty(joined);
        });
        purposeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurpose()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));
        approvalColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus().name()));
    }

    /**
     * Configure the table rows
     */
    private void setupTableRows() {
        recordsTableView.setRowFactory(tv -> {
            TableRow<LendingRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    LendingRecord selectedRecord = row.getItem();
                    showPopup(selectedRecord);
                }
            });
            return row;
        });
    }


//    /**
//     * Populates the status and approval combo boxes with available options.
//     */
//    @FXML
//    private void setupComboBoxes() {
//        statusComboBox.getItems().addAll("BORROWED", "RETURNED", "OVERDUE");
//        approvalComboBox.getItems().addAll("APPROVED", "PENDING");
//    }

    /**
     * Retrieves and filters lending records based on student-specific criteria.
     */
    @FXML
    private void loadLendingRecords() {
        try {

            String borrowerId = null;
            borrowerId = this.borrowerIdField.getText() != null ? this.borrowerIdField.getText() : null;
            String responsibleAcademicId = this.responsibleAcademicIdField.getText() != null ? this.responsibleAcademicIdField.getText() : null;

            LocalDate borrowStart = borrowStartPicker.getValue();
            LocalDate borrowEnd = borrowEndPicker.getValue();
            LocalDate returnStart = returnStartPicker.getValue();
            LocalDate returnEnd = returnEndPicker.getValue();

            Date borrowStartDate = (borrowStart != null) ? Date.from(borrowStart.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date borrowEndDate = (borrowEnd != null) ? Date.from(borrowEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date returnStartDate = (returnStart != null) ? Date.from(returnStart.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date returnEndDate = (returnEnd != null) ? Date.from(returnEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

            LendingRecordStatus status = (statusComboBox.getValue() != null && !statusComboBox.getValue().equals("Any"))
                    ? LendingRecordStatus.valueOf(statusComboBox.getValue())
                    : null;

            approvalStatus approval = (approvalComboBox.getValue() != null && !approvalComboBox.getValue().equals("Any"))
                    ? approvalStatus.valueOf(approvalComboBox.getValue())
                    : null;

            List<LendingRecord> records = lendingService.filterLendingRecords(
                    borrowerId,
                    responsibleAcademicId,
                    borrowStartDate, borrowEndDate,
                    returnStartDate, returnEndDate,
                    status, approval
            );

            System.out.println("Records: " + records.size());

            recordsTableView.getItems().setAll(records);
            setupTableColumns();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load lending records.");
            e.printStackTrace();
        }
    }

    /**
     * Add new lending record
     */
    @FXML
    private void handleAddLendingRecord() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageRecords/admin_add_lending_record.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminAddLendingRecordController controller = loader.getController();

            controller.initialize(lendingService); // Pass LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Lending Records");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 400, 400));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();

            // Refresh table
            loadLendingRecords();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    /**
     * Handles the closing of the current window.
     */
    @FXML
    private void handleCloseWindow() {
        Stage stage = (Stage) recordsTableView.getScene().getWindow();
        stage.close();
    }

    /**
     * Utility method for displaying alert dialogs.
     */
    @FXML
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPopup(LendingRecord record) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageRecords/lending_record_row_popup.fxml"));
            Parent root = loader.load();

            LendingRecordRowPopupController controller = loader.getController();
            controller.setData(lendingService, record);

            Stage popupStage = new Stage();
            popupStage.setTitle("Edit Lending Record");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            // Refresh the table after popup closes
            loadLendingRecords();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load popup.");
        }
    }

    private void addLendingRecord() {

    }
}
