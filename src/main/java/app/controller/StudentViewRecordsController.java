package app.controller;

import app.model.AppUser;
import app.model.Course;
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

public class StudentViewRecordsController {

    @FXML private TableView<LendingRecord> recordsTableView;

    @FXML private TableColumn<LendingRecord, String> academicIdColumn;
    @FXML private TableColumn<LendingRecord, String> borrowDateColumn;
    @FXML private TableColumn<LendingRecord, String> returnDateColumn;
    @FXML private TableColumn<LendingRecord, String> equipmentColumn;
    @FXML private TableColumn<LendingRecord, String> purposeColumn;
    @FXML private TableColumn<LendingRecord, String> statusColumn;
    @FXML private TableColumn<LendingRecord, String> approvalColumn;

    @FXML private TextField courseIdField;
    @FXML private DatePicker borrowStartPicker;
    @FXML private DatePicker borrowEndPicker;
    @FXML private DatePicker returnStartPicker;
    @FXML private DatePicker returnEndPicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> approvalComboBox;

    private AppUser appUser;
    private LendingService lendingService;

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

//        setupTableColumns();
//        setupComboBoxes();
    }

    public void setAppUser(AppUser user) {
        this.appUser = user;
        loadLendingRecords();
    }
    @FXML
    private void setupTableColumns() {
        academicIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getResponsibleAcademic()));
        borrowDateColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getBorrowDate())));
        returnDateColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getReturnDate())));
        equipmentColumn.setCellValueFactory(data -> {
            Set<String> equipmentSet = data.getValue().getBorrowedEquipment();
            String joined = equipmentSet != null ? String.join(", ", equipmentSet) : "";
            return new SimpleStringProperty(joined);
        });
        purposeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurpose()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().name()));
        approvalColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus().name()));
    }
    @FXML
    private void setupComboBoxes() {
        statusComboBox.getItems().addAll("BORROWED", "RETURNED", "OVERDUE");
        approvalComboBox.getItems().addAll("APPROVED", "PENDING");
    }

    @FXML
    private void loadLendingRecords() {
        if (appUser == null) return;

        try {
            String personId = appUser != null ? appUser.getPersonId() : null;
            String courseId = (courseIdField.getText() != null && !courseIdField.getText().trim().isEmpty()) ? courseIdField.getText() : null;
            LocalDate borrowStart = (borrowStartPicker.getValue() != null) ? borrowStartPicker.getValue() : null;
            LocalDate borrowEnd = (borrowEndPicker.getValue() != null) ? borrowEndPicker.getValue() : null;
            LocalDate returnStart = (returnStartPicker.getValue() != null) ? returnStartPicker.getValue() : null;
            LocalDate returnEnd = (returnEndPicker.getValue() != null) ? returnEndPicker.getValue() : null;

            String academicId = null;

            // Convert course to academic
            if (courseId != null) {
                CourseService courseService = new CourseService(Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager());
                Course course = courseService.findByCourseId(courseId);
                if (course != null) {
                    academicId = course.getAcademicId();
                }
                else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Course not found.");
                    return;
                }
            }

            // Convert LocalDate to java.util.Date
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
                    academicId,
                    borrowStartDate, borrowEndDate,
                    returnStartDate, returnEndDate,
                    status, approval
            );
            //Debug
            System.out.println("Records: " + records.size());
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
    @FXML
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
