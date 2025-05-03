package app.controller;

import app.model.AppUser;
import app.model.Student;
import app.service.LendingService;
import app.util.LogoutHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class StudentController implements LogoutHandler {

    private AppUser appUser;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize(AppUser appUser) {
        this.appUser = appUser;
        welcomeLabel.setText("Welcome, " + appUser.getUsername() + " !");
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
            // Optionally, handle the exception with an alert box or error log
        }
    }

    @FXML
    private void handleLendingRequest() {
        try {
            // Load the FXML file for the lending request form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student_lending_request_form.fxml"));
            Parent root = loader.load();

            // Initialize controller
            StudentLendingRequestController controller = loader.getController();
            controller.initialize(appUser);

            // Create a new Stage for the popup window
            Stage popupStage = new Stage();
            popupStage.setTitle("Lending Request Form");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root));

            // Set modality to block interaction with the main window
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Show the popup window and wait until it's closed
            popupStage.showAndWait();
        } catch (IOException e) {
            // Log or handle the exception appropriately
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewRecords() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student_view_records.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            StudentViewRecordsController controller = loader.getController();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student_profile_management.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the appUser
            StudentProfileManagementController controller = loader.getController();
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
        logout(welcomeLabel); // Call the default method from the interface
    }

}