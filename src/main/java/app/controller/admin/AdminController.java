package app.controller.admin;

import app.controller.admin.ManageAcademics.AdminManageAcademicsController;
import app.controller.admin.ManageCourses.AdminManageCoursesController;
import app.controller.admin.ManageEquipment.AdminManageEquipmentController;
import app.controller.admin.ManageProfessionals.AdminManageProfessionalsController;
import app.controller.admin.ManageRecords.AdminManageRecordsController;
import app.controller.admin.ManageStudents.AdminAddStudentController;
import app.controller.admin.ManageStudents.AdminManageStudentsController;
import app.service.*;
import app.util.LogoutHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController implements LogoutHandler {


    private LendingService lendingService;
    private InventoryService inventoryService;
    private EquipmentImageService equipmentImageService;

    // FXML Components
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        // Initialize the welcome message with admin username if available
        welcomeLabel.setText("Welcome, Admin");

        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        InventoryService inventoryService = new InventoryService(entityManager);
        StudentService studentService = new StudentService(entityManager);
        AcademicService academicService = new AcademicService(entityManager);
        CourseService courseService = new CourseService(entityManager);
        EquipmentImageService equipmentImageService = new EquipmentImageService(entityManager);

        this.equipmentImageService = equipmentImageService;
        this.lendingService = new LendingService(entityManager, inventoryService, studentService, academicService, courseService);
        this.inventoryService = inventoryService;
    }

    // Button Handlers
    @FXML
    private void handleManageStudents() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageStudents/admin_manage_students.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminManageStudentsController controller = loader.getController();

            controller.initialize();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Students");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageAcademics() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageAcademics/admin_manage_academics.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminManageAcademicsController controller = loader.getController();

            controller.initialize();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Academics");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageProfessionals() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageProfessionals/admin_manage_professionals.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminManageProfessionalsController controller = loader.getController();

            controller.initialize();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Professionals");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageCourses() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageCourses/admin_manage_courses.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminManageCoursesController controller = loader.getController();

            controller.initialize();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Courses");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageLendingRecords() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageRecords/admin_manage_records.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminManageRecordsController controller = loader.getController();

            controller.initialize(lendingService); // Pass LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Lending Records");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageEquipment() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageEquipment/admin_manage_equipment.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminManageEquipmentController controller = loader.getController();

            controller.initialize(inventoryService, equipmentImageService); // Pass LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Equipment");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSystemAnalytics() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/system_analytics.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            SystemAnalyticsController controller = loader.getController();

//            controller.initialize(); // Pass LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("System Analytics");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 1200, 800));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        logout(welcomeLabel);
    }

    // Additional methods if needed
    /*
    private void initializeAdminData() {
        // Load any initial data needed for admin dashboard
    }
    */
}
