package app.controller.academic;

import app.model.AppUser;
import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class AcademicViewRequestsController {

    @FXML private TableView<LendingRecord> recordsTableView;

    @FXML private TableColumn<LendingRecord, String> studentIdColumn;
    @FXML private TableColumn<LendingRecord, String> borrowDateColumn;
    @FXML private TableColumn<LendingRecord, String> returnDateColumn;
    @FXML private TableColumn<LendingRecord, String> equipmentColumn;
    @FXML private TableColumn<LendingRecord, String> purposeColumn;
    @FXML private TableColumn<LendingRecord, String> statusColumn;
    @FXML private TableColumn<LendingRecord, String> approvalColumn;
    @FXML private TableColumn<LendingRecord, Void> actionColumn;

    @FXML private DatePicker borrowStartPicker;
    @FXML private DatePicker borrowEndPicker;
    @FXML private DatePicker returnStartPicker;
    @FXML private DatePicker returnEndPicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField studentIdField;

    private AppUser appUser;
    private LendingService lendingService;

    @FXML
    public void initialize(AppUser appUser) {
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();

        InventoryService inventoryService = new InventoryService(entityManager);
        StudentService studentService = new StudentService(entityManager);
        AcademicService academicService = new AcademicService(entityManager);
        CourseService courseService = new CourseService(entityManager);

        this.appUser = appUser;
        this.lendingService = new LendingService(entityManager, inventoryService, studentService, academicService, courseService);

        loadLendingRequests();
        setupComboBoxes();
        setupTableColumns();
    }

    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList("Any", "BORROWED", "RETURNED", "OVERDUE"));
        statusComboBox.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        studentIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBorrower()));
        borrowDateColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBorrowDate())));
        returnDateColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getReturnDate())));
        equipmentColumn.setCellValueFactory(data -> {
            Set<String> equipmentSet = data.getValue().getBorrowedEquipment();
            return new SimpleStringProperty(equipmentSet != null ? String.join(", ", equipmentSet) : "");
        });
        purposeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurpose()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));
        approvalColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus().name()));

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button declineBtn = new Button("Decline");
            private final HBox box = new HBox(5, approveBtn, declineBtn);

            {
                approveBtn.setOnAction(e -> {
                    try {
                        LendingRecord record = getTableView().getItems().get(getIndex());
                        lendingService.approveLendingRecord(record);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Successfully approved lending record!");
                        loadLendingRequests();
                        setupTableColumns();
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.INFORMATION, "Error", ex.getMessage() != null ? ex.getMessage() : "Failed to approve lending record!");
                    }
                });

                declineBtn.setOnAction(e -> {
                    LendingRecord record = getTableView().getItems().get(getIndex());
                    lendingService.removeLendingRecord(record);
                    loadLendingRequests();
                    setupTableColumns();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
    }

    @FXML
    private void loadLendingRequests() {
        try {
            String studentId = (studentIdField.getText().trim() != null) ? studentIdField.getText().trim() : null;
            LocalDate borrowStart = borrowStartPicker.getValue();
            LocalDate borrowEnd = borrowEndPicker.getValue();
            LocalDate returnStart = returnStartPicker.getValue();
            LocalDate returnEnd = returnEndPicker.getValue();

            Date borrowStartDate = (borrowStart != null) ? Date.from(borrowStart.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date borrowEndDate = (borrowEnd != null) ? Date.from(borrowEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date returnStartDate = (returnStart != null) ? Date.from(returnStart.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date returnEndDate = (returnEnd != null) ? Date.from(returnEnd.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;

            LendingRecordStatus status = (!"Any".equals(statusComboBox.getValue()) && statusComboBox.getValue() != null)
                    ? LendingRecordStatus.valueOf(statusComboBox.getValue())
                    : null;

            List<LendingRecord> records = lendingService.filterLendingRecords(
                    studentId,
                    this.appUser.getPersonId(),
                    borrowStartDate, borrowEndDate,
                    returnStartDate, returnEndDate,
                    status, approvalStatus.PENDING
            );

            recordsTableView.getItems().setAll(records);
            setupTableColumns();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load lending records.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseWindow() {
        Stage stage = (Stage) recordsTableView.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
