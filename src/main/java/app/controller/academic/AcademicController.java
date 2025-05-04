package app.controller.academic;

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
            popupStage.setScene(new Scene(root));

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

    }

    @FXML
    private void handleLogout() {
        logout(welcomeLabel); // Use LogoutHandler's default method
    }
}
