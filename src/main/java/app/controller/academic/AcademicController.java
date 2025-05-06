package app.controller.academic;

import app.controller.student.StudentProfileManagementController;
import app.model.Academic;
import app.model.AppUser;

import app.service.AcademicService;
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

public class AcademicController implements LogoutHandler {

    private AppUser appUser;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize(AppUser appUser) {
        this.appUser = appUser;
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();
        AcademicService academicService = new AcademicService(entityManager);
        Academic academic = academicService.findByPersonId(appUser.getPersonId());
        welcomeLabel.setText("Welcome, " + academic.getFullName() + " !");
    }

    @FXML
    private void handleSearchForEquipment() {
        try {
            // Load the guest_dashboard.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/guest_dashboard.fxml"));
            Parent root = loader.load();

            // Create a new Stage for the popup window
            Stage popupStage = new Stage();
            popupStage.setTitle("Equipment Search");

            // Set the popup scene
            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            // Make the popup modal, blocking interaction with other windows
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Show the popup and wait until it is closed
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAcademicBorrowEquipment() {
        try {
            // Load the FXML file for the academic lending request form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academic/academic_borrow_equipment.fxml"));
            Parent root = loader.load();

            // Initialize controller
            AcademicBorrowEquipmentController controller = loader.getController();
            controller.initialize(appUser);

            // Create a new Stage for the popup window
            Stage popupStage = new Stage();
            popupStage.setTitle("Academic Lending Request Form");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 400, 400));

            // Set modality to block interaction with the main window
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Show the popup window and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewRecords() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academic/academic_view_records.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AcademicViewRecordsController controller = loader.getController();

            controller.initialize(appUser); // Pass appUser and LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("View Lending Records");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewRequests() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academic/academic_view_requests.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AcademicViewRequestsController controller = loader.getController();

            controller.initialize(appUser); // Pass appUser and LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("View Student Requests");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewSupervisedRecords() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academic/academic_view_supervised_records.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AcademicViewSupervisedRecordsController controller = loader.getController();

            controller.initialize(appUser); // Pass appUser and LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("View Supervised Lending Records");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAcademicStats() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academic/academic_stats.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AcademicStatsController controller = loader.getController();

            controller.initialize(appUser); // Pass appUser and LendingService

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("View Academic Stats");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileManagement() {
        try {
            // Load the profile management FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/academic/academic_profile_management.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the appUser
            AcademicProfileManagementController controller = loader.getController();
            controller.initialize(appUser);  // Pass the logged-in user to the controller

            // Create a popup stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Manage Profile");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);  // Modal window
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an alert if the FXML fails to load
        }
    }


    @FXML
    private void handleLogout() {
        logout(welcomeLabel); // Use LogoutHandler's default method
    }
}
