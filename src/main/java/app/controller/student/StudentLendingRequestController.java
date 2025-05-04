package app.controller.student;

import app.model.AppUser;
import app.model.Course;
import app.model.LendingRecord;
import app.model.Student;
import app.model.enums.LendingRecordStatus;
import app.service.LendingService;
import app.service.StudentService;
import app.service.AcademicService;
import app.service.InventoryService;
import app.service.CourseService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class StudentLendingRequestController {

    private Student student;
    private EntityManager entityManager;
    private InventoryService inventoryService;
    private StudentService studentService;
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
    private TextField courseIdField;

    @FXML
    private TextField purposeField;

    @FXML
    public void initialize(AppUser appUser) {
        // Initialize the EntityManager
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();

        // Initialize other dependent services (if needed)
        InventoryService inventoryService = new InventoryService(entityManager);
        StudentService studentService = new StudentService(entityManager);
        AcademicService academicService = new AcademicService(entityManager);
        CourseService courseService = new CourseService(entityManager);

        // Initialize LendingService or equivalent service classes
        LendingService lendingService = new LendingService(entityManager, inventoryService, studentService, academicService, courseService);

        this.student = studentService.findByPersonId(appUser.getPersonId());
        this.entityManager = entityManager;
        this.inventoryService = inventoryService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.courseService = courseService;
        this.lendingService = lendingService;
    }
        @FXML
    private void handleSubmit() {
        try {
            // Validate inputs
            String equipmentIdsInput = equipmentIdsField.getText().trim();
            LocalDate borrowDate = borrowDatePicker.getValue();
            LocalDate returnDate = returnDatePicker.getValue();
            String courseId = courseIdField.getText().trim();
            String purpose = purposeField.getText().trim();

            // Find course
            Course course = courseService.findByCourseId(courseId);

            // Check if course exists
            if (course == null) {
                showAlert("Error", "Course not found. Student must be enrolled in a course to submit a lending request.", Alert.AlertType.INFORMATION);
                return;
            }

            // Check if student is enrolled in the course
            if (!course.isStudentEnrolled(student.getPersonId())) {
                showAlert("Error", "Student is not enrolled in the course. Please contact the course coordinator.", Alert.AlertType.INFORMATION);
                return;
            }

            // Validate dates
            if (!lendingService.validateDates(student.getPersonId(), java.sql.Date.valueOf(borrowDate), java.sql.Date.valueOf(returnDate))) {
                showAlert("Error", "Students should not borrow for more than 2 weeks ", Alert.AlertType.INFORMATION);
                return;
            }

            String academicId = course.getAcademicId();

            if (equipmentIdsInput.isEmpty() || borrowDate == null || returnDate == null || academicId.isEmpty()) {
                showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
                return;
            }

            // Parse Equipment IDs into a Set
            Set<String> equipmentIds = new HashSet<>();
            for (String id : equipmentIdsInput.split(",")) {
                equipmentIds.add(id.trim());
            }

            // Create a LendingRecord instance (could be sent to a service or saved to the database later)
            LendingRecord lendingRecord = new LendingRecord(
                    null,  // Generate an ID or use a UUID
                    this.student.getPersonId(),  // Replace with the actual student ID
                    equipmentIds,
                    academicId,
                    java.sql.Date.valueOf(borrowDate),
                    java.sql.Date.valueOf(returnDate),
                    LendingRecordStatus.BORROWED, // Initial status
                    purpose
            );

            // Save the record

            LendingService lendingService = new LendingService(
                    this.entityManager,
                    this.inventoryService,
                    this.studentService,
                    this.academicService,
                    this.courseService
            );
            lendingService.saveRecord(lendingRecord);

            // Close the form
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