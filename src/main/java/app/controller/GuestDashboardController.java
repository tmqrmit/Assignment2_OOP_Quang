package app.controller;

import app.model.Equipment;
import app.service.EquipmentImageService;
import app.service.InventoryService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GuestDashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> conditionFilter;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private TableView<Equipment> equipmentTable;

    @FXML
    private TableColumn<Equipment, String> colID;

    @FXML
    private TableColumn<Equipment, String> colName;

    @FXML
    private TableColumn<Equipment, String> colStatus;

    @FXML
    private TableColumn<Equipment, String> colPurchaseDate;

    @FXML
    private TableColumn<Equipment, String> colCondition;

    @FXML
    private TableColumn<Equipment, Void> colViewImage;

    private InventoryService inventoryService;
    private EquipmentImageService equipmentImageService;

    @FXML
    public void initialize() {
        // Setup EntityManager and services
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("your-persistence-unit");
        EntityManager em = emf.createEntityManager();
        inventoryService = new InventoryService(em);
        equipmentImageService = new EquipmentImageService(em);

        // Initialize table columns
        colID.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPurchaseDate.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getPurchaseDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return new SimpleStringProperty(date != null ? dateFormat.format(date) : "");
        });
        colCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));

        // Set up the "View Image" column with buttons
        setUpViewImageColumn();

        // Initialize ComboBoxes with default values
        conditionFilter.getSelectionModel().select("All");
        statusFilter.getSelectionModel().select("All");

        // Load all equipment by default
        loadAllEquipment();
    }

    /**
     * Sets up the "View Image" column with a button to display each equipment's image in a pop-up.
     */
    private void setUpViewImageColumn() {
        colViewImage.setCellFactory(param -> new TableCell<>() {
            private final Button viewImageButton = new Button("View Image");

            {
                // Define button's action
                viewImageButton.setOnAction(event -> {
                    Equipment selectedEquipment = getTableView().getItems().get(getIndex());
                    if (selectedEquipment != null) {
                        displayEquipmentImage(selectedEquipment.getEquipmentId());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Empty cell
                } else {
                    setGraphic(viewImageButton); // Set the "View Image" button
                }
            }
        });
    }

    /**
     * Displays the equipment image in a new pop-up window.
     *
     * @param equipmentId The associated equipment ID.
     */
    private void displayEquipmentImage(String equipmentId) {
        // Use EquipmentImageService to retrieve and display the image
        String base64Image = equipmentImageService.retrieveAndDisplayImage(equipmentId);

        if (!"No image available".equals(base64Image)) {
            // Convert the Base64 string to a JavaFX Image for display
            byte[] imageData = Base64.getDecoder().decode(base64Image.split(",")[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            Image image = new Image(bis);

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();
            popupStage.setTitle("Equipment Image");

            // Set up an ImageView to display the image
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(500); // Adjust width for display
            imageView.setFitHeight(400); // Adjust height for display

            // Create a layout and add the ImageView
            StackPane root = new StackPane(imageView);
            Scene scene = new Scene(root, 550, 450); // Adjust size of the pop-up window

            // Set the scene and show the stage
            popupStage.setScene(scene);
            popupStage.show();
        } else {
            // No image found for the equipment, show alert
            showAlert("No Image Found", "No image is available for the selected equipment.");
        }
    }

    /**
     * Displays an alert dialog for user feedback.
     *
     * @param title   The title of the alert window.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleShowAll() {
        loadAllEquipment();
    }

    @FXML
    public void handleApplyFilters() {
        String searchName = searchField.getText();
        String selectedCondition = conditionFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        List<Equipment> filteredEquipment = inventoryService.findAll().stream()
                .filter(e -> {
                    // Filter by name
                    if (searchName == null || searchName.trim().isEmpty()) {
                        return true;
                    }
                    return e.getName() != null && e.getName().toLowerCase().contains(searchName.toLowerCase());
                })
                .filter(e -> {
                    // Filter by condition
                    if ("All".equalsIgnoreCase(selectedCondition) || selectedCondition == null || e.getCondition() == null) {
                        return true;
                    }
                    return e.getCondition() != null && e.getCondition().toString().equalsIgnoreCase(selectedCondition);
                })
                .filter(e -> {
                    // Filter by status
                    if ("All".equalsIgnoreCase(selectedStatus) || selectedStatus == null || e.getStatus() == null) {
                        return true;
                    }
                    return e.getStatus() != null && e.getStatus().toString().equalsIgnoreCase(selectedStatus);
                })
                .collect(Collectors.toList());

        displayEquipment(filteredEquipment);
    }

    private void loadAllEquipment() {
        List<Equipment> equipmentList = inventoryService.findAll();
        displayEquipment(equipmentList);
    }

    private void displayEquipment(List<Equipment> equipmentList) {
        ObservableList<Equipment> observableEquipment = FXCollections.observableArrayList(equipmentList);
        equipmentTable.setItems(observableEquipment);
        setUpViewImageColumn();
    }
}