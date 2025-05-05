package app.controller.academic;

import app.model.AppUser;
import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Controller responsible for managing the display and filtering
 * of lending records for a student user within the application.
 */
public class AcademicViewRecordsController {

    @FXML private TableView<LendingRecord> recordsTableView;

    @FXML private TableColumn<LendingRecord, String> academicIdColumn;
    @FXML private TableColumn<LendingRecord, String> borrowDateColumn;
    @FXML private TableColumn<LendingRecord, String> returnDateColumn;
    @FXML private TableColumn<LendingRecord, String> equipmentColumn;
    @FXML private TableColumn<LendingRecord, String> purposeColumn;
    @FXML private TableColumn<LendingRecord, String> statusColumn;
    @FXML private TableColumn<LendingRecord, String> approvalColumn;

    @FXML private DatePicker borrowStartPicker;
    @FXML private DatePicker borrowEndPicker;
    @FXML private DatePicker returnStartPicker;
    @FXML private DatePicker returnEndPicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> approvalComboBox;

    private AppUser appUser;
    private LendingService lendingService;

    /**
     * Initializes the controller with required services and dependencies.
     * @param appUser the currently logged-in student user
     */
    @FXML
    public void initialize(AppUser appUser) {
        this.appUser = appUser;

        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();

        InventoryService inventoryService = new InventoryService(entityManager);
        StudentService studentService = new StudentService(entityManager);
        AcademicService academicService = new AcademicService(entityManager);
        CourseService courseService = new CourseService(entityManager);

        this.lendingService = new LendingService(entityManager, inventoryService, studentService, academicService, courseService);

        loadLendingRecords();
        setupTableColumns();
        setupComboBoxes();
    }

    public void setAppUser(AppUser user) {
        this.appUser = user;
        loadLendingRecords();
    }

    /**
     * Configures the columns of the lending record table.
     */
    @FXML
    private void setupTableColumns() {
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
     * Populates the status and approval combo boxes with available options.
     */
    @FXML
    private void setupComboBoxes() {
        statusComboBox.getItems().addAll("BORROWED", "RETURNED", "OVERDUE");
        approvalComboBox.getItems().addAll("APPROVED", "PENDING");
    }

    /**
     * Retrieves and filters lending records based on student-specific criteria.
     */
    @FXML
    private void loadLendingRecords() {
        if (appUser == null) return;

        try {
            String personId = appUser.getPersonId();

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
                    personId,
                    null,  // academicId is not used in this context
                    borrowStartDate, borrowEndDate,
                    returnStartDate, returnEndDate,
                    status, approval
            );

            recordsTableView.getItems().setAll(records);
            setupTableColumns();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load lending records.");
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

}
