package app.controller.professional;

import app.model.AppUser;
import app.model.Professional;
import app.service.ProfessionalService;

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

public class ProfessionalController implements LogoutHandler {

    private AppUser appUser;
    private Professional professional;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize(AppUser appUser) {
        this.appUser = appUser;
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();
         ProfessionalService professionalService= new ProfessionalService(entityManager);
        Professional professional = professionalService.findByPersonId(appUser.getPersonId());
        this.professional = professional;
        welcomeLabel.setText("Welcome, " + professional.getFullName() + " !");
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
    private void handleProfessionalBorrowEquipment() {
        try {
            // Load the FXML file for the academic lending request form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professional/professional_borrow_equipment.fxml"));
            Parent root = loader.load();

            // Initialize controller
            ProfessionalBorrowEquipmentController controller = loader.getController();
            controller.initialize(appUser);

            // Create a new Stage for the popup window
            Stage popupStage = new Stage();
            popupStage.setTitle("Professional Lending Request Form");

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professional/professional_view_records.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            ProfessionalViewRecordsController controller = loader.getController();

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
    private void handleProfileManagement() {
        try {
            // Load the profile management FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/professional/professional_profile_management.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the appUser
            ProfessionalProfileManagementController controller = loader.getController();
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
