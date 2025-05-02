package app.controller;

import app.model.AppUser;
import app.model.enums.Role; // Import Role enum for role-based logic
import app.service.AppUserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Hyperlink guestLink;

    private final AppUserService appUserService;

    public LoginController() {
        // Set up the EntityManager and UserService
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        appUserService = new AppUserService(entityManager);
    }

    @FXML
    private void initialize() {
        // Set the action for the "Continue as Guest" hyperlink
        guestLink.setOnAction(event -> handleContinueAsGuest());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation logic to ensure fields are not empty
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("All fields are required!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Retrieve the AppUser from the database
        AppUser user = appUserService.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            // Login successful
            statusLabel.setText("Login successful!");
            statusLabel.setStyle("-fx-text-fill: green;");

            // Redirect to the user-specific dashboard based on their role
            loadDashboardByRole(user);

        } else {
            // Invalid login
            statusLabel.setText("Invalid username or password!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    @FXML
    private void handleContinueAsGuest() {
        try {
            // Load the Guest Dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/guest_dashboard.fxml"));
            Parent guestDashboard = loader.load();

            // Get the current stage from the hyperlink's scene
            Stage stage = (Stage) guestLink.getScene().getWindow();

            // Set the new scene for the Guest Dashboard
            Scene scene = new Scene(guestDashboard);
            stage.setScene(scene);
            stage.setTitle("Guest Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load Guest Dashboard.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    @FXML
    private void loadDashboardByRole(AppUser appUser) {
        try {
            // Determine the FXML file to load based on the user's role
            String fxmlFile;
            switch (appUser.getRole()) {
                case STUDENT -> fxmlFile = "/student_dashboard.fxml";
                case ACADEMIC -> fxmlFile = "/teacher_dashboard.fxml";
                case ADMIN -> fxmlFile = "/admin_dashboard.fxml";
                case PROFESSIONAL -> fxmlFile = "/admin_dashboard.fxml";
                default -> fxmlFile = "/guest_dashboard.fxml";
            }

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent dashboard = loader.load();

            // Access the dashboard's controller
            Object controller = loader.getController();

            // Get the AppUser object
            if (controller instanceof StudentController) {
                ((StudentController) controller).initialize(appUser);
            }

            // Get the current stage from the login form's scene
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Set the new scene for the dashboard
            Scene scene = new Scene(dashboard);
            stage.setScene(scene);
            stage.setTitle(appUser.getRole() + " Dashboard"); // Set the window's title to match the role
        } catch (Exception e) {
            e.printStackTrace();
            // Show an error message if the dashboard fails to load
            statusLabel.setText("Failed to load the dashboard for role: " + appUser.getRole());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}