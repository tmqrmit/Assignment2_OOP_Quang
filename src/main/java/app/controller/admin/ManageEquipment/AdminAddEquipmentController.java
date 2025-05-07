package app.controller.admin.ManageEquipment;

import app.model.Equipment;
import app.model.enums.EquipmentCondition;
import app.model.enums.EquipmentStatus;
import app.service.InventoryService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AdminAddEquipmentController {

    @FXML
    private TextField equipmentIdField;

    @FXML
    private TextField equipmentNameField;

    @FXML
    private DatePicker purchaseDateField;

    @FXML
    private ComboBox<EquipmentStatus> statusComboBox;

    @FXML
    private ComboBox<EquipmentCondition> conditionComboBox;

    private InventoryService inventoryService;

    @FXML
    public void initialize(InventoryService inventoryService) {

        this.inventoryService = inventoryService;

        // Populate status and condition combo boxes
        statusComboBox.getItems().addAll(EquipmentStatus.values());
        conditionComboBox.getItems().addAll(EquipmentCondition.values());
    }

    @FXML
    private void handleAdd() {
        String equipmentId = equipmentIdField.getText();
        String equipmentName = equipmentNameField.getText();
        Date purchaseDate = Date.from(purchaseDateField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        EquipmentStatus status = statusComboBox.getValue();
        EquipmentCondition condition = conditionComboBox.getValue();

        if (equipmentId == null || equipmentId.isEmpty() ||
                equipmentName == null || equipmentName.isEmpty() ||
                purchaseDate == null ||
                status == null ||
                condition == null) {

            showAlert(AlertType.ERROR, "Missing Fields", "Please fill in all the fields.");
            return;
        }

        Equipment equipment = new Equipment(equipmentId, equipmentName, status, purchaseDate, condition);
        inventoryService.saveEquipment(equipment);
        showAlert(AlertType.INFORMATION, "Success", "Equipment added successfully.");

        clearForm();
    }

    private void clearForm() {
        equipmentIdField.clear();
        equipmentNameField.clear();
        purchaseDateField.setValue(null);
        statusComboBox.setValue(null);
        conditionComboBox.setValue(null);
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
