package app.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.IOException;

public interface LogoutHandler {

    default void logout(Label referenceLabel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) referenceLabel.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show a custom alert here
        }
    }
}
