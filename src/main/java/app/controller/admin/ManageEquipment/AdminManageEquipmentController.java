package app.controller.admin.ManageEquipment;

import app.controller.admin.ManageRecords.LendingRecordRowPopupController;
import app.model.Equipment;
import app.model.EquipmentImage;
import app.model.LendingRecord;
import app.service.EquipmentImageService;
import app.service.InventoryService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AdminManageEquipmentController {

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

    @FXML
    private TableColumn<Equipment, Void> colUploadImage;

    private InventoryService inventoryService;
    private EquipmentImageService equipmentImageService;

    @FXML
    public void initialize(InventoryService inventoryService, EquipmentImageService equipmentImageService) {
        this.inventoryService = inventoryService;
        this.equipmentImageService = equipmentImageService;

        setUpTableColumns();

        setUpViewImageColumn();
        setUpUploadImageColumn();

        conditionFilter.getSelectionModel().select("All");
        statusFilter.getSelectionModel().select("All");

        loadAllEquipment();
        setupTableRows();
    }

    /**
     * Set up table columns
     */
    @FXML
    private void setUpTableColumns() {
        colID.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPurchaseDate.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getPurchaseDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return new SimpleStringProperty(date != null ? dateFormat.format(date) : "");
        });
        colCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
    }


    /**
     * Configure the table rows
     */
    @FXML
    private void setupTableRows() {
        equipmentTable.setRowFactory(tv -> {
            TableRow<Equipment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Equipment selectedEquipment = row.getItem();
                    this.showPopup(selectedEquipment);
                }
            });
            return row;
        });
    }

    private void setUpViewImageColumn() {
        colViewImage.setCellFactory(param -> new TableCell<>() {
            private final Button viewImageButton = new Button("View Image");

            {
                viewImageButton.setOnAction(event -> {
                    Equipment selected = getTableView().getItems().get(getIndex());
                    if (selected != null) displayEquipmentImage(selected.getEquipmentId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewImageButton);
            }
        });
    }

    private void setUpUploadImageColumn() {
        colUploadImage.setCellFactory(param -> new TableCell<>() {
            private final Button uploadButton = new Button("Upload Image");

            {
                uploadButton.setOnAction(event -> {
                    Equipment selected = getTableView().getItems().get(getIndex());
                    if (selected != null) uploadImageForEquipment(selected.getEquipmentId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : uploadButton);
            }
        });
    }

    private void displayEquipmentImage(String equipmentId) {
        String base64Image = equipmentImageService.retrieveAndDisplayImage(equipmentId);
        if (!"No image available".equals(base64Image)) {
            byte[] data = Base64.getDecoder().decode(base64Image.split(",")[1]);
            Image image = new Image(new ByteArrayInputStream(data));
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(500);
            imageView.setFitHeight(400);

            Stage popup = new Stage();
            popup.setTitle("Equipment Image");
            popup.setScene(new Scene(new StackPane(imageView), 550, 450));
            popup.show();
        } else {
            showAlert("No Image", "No image is available for this equipment.");
        }
    }

    private void uploadImageForEquipment(String equipmentId) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image to Upload");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());

                // Dynamically detect MIME type
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null) {
                    mimeType = "image/png"; // fallback
                }

                // Fetch the Equipment entity first
                Equipment equipment = inventoryService.findByEquipmentId(equipmentId);
                if (equipment == null) {
                    showAlert("Error", "Equipment not found with ID: " + equipmentId);
                    return;
                }

                // Construct and save EquipmentImage
                EquipmentImage image = new EquipmentImage(equipment, imageBytes, file.getName(), mimeType);
                equipmentImageService.saveEquipmentImage(image); // you must create this method

                showAlert("Success", "Image uploaded successfully.");
            } catch (Exception e) {
                showAlert("Error", "Failed to upload image: " + e.getMessage());
            }
        }
    }

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
        String search = searchField.getText();
        String cond = conditionFilter.getValue();
        String status = statusFilter.getValue();

        List<Equipment> filtered = inventoryService.findAll().stream()
                .filter(e -> search == null || search.isEmpty() || (e.getName() != null && e.getName().toLowerCase().contains(search.toLowerCase())))
                .filter(e -> cond == null || "All".equals(cond) || (e.getCondition() != null && e.getCondition().name().equalsIgnoreCase(cond)))
                .filter(e -> status == null || "All".equals(status) || (e.getStatus() != null && e.getStatus().name().equalsIgnoreCase(status)))
                .collect(Collectors.toList());

        displayEquipment(filtered);
    }

    private void loadAllEquipment() {
        displayEquipment(inventoryService.findAll());
    }

    private void displayEquipment(List<Equipment> list) {
        ObservableList<Equipment> observableList = FXCollections.observableArrayList(list);
        equipmentTable.setItems(observableList);
        setUpViewImageColumn();
        setUpUploadImageColumn();
    }

    private void showPopup(Equipment selectedEquipment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageEquipment/equipment_row_popup.fxml"));
            Parent root = loader.load();

            EquipmentRowPopupController controller = loader.getController();
            controller.initialize(inventoryService, selectedEquipment);

            Stage popupStage = new Stage();
            popupStage.setTitle("Edit Equipment");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            loadAllEquipment();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load popup.");
        }
    }

}
