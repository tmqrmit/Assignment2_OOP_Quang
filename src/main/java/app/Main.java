package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main extends Application  {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // Set up and display the scene
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}