package app.controller.admin.ManageEquipment;

import app.model.Equipment;
import app.model.enums.EquipmentCondition;
import app.model.enums.EquipmentStatus;
import app.service.InventoryService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class EquipmentRowPopupController {

    @FXML
    private TextField equipmentIdField;

    @FXML
    private TextField equipmentNameField;

    @FXML
    private ComboBox<EquipmentStatus> statusComboBox;

    @FXML
    private DatePicker purchaseDateField;

    @FXML
    private ComboBox<EquipmentCondition> conditionComboBox;

    private InventoryService inventoryService;

    private Equipment selectedEquipment;


    @FXML
    public void initialize(InventoryService inventoryService, Equipment equipment) {
        // Populate combo boxes with enum values
        statusComboBox.getItems().addAll(EquipmentStatus.values());
        conditionComboBox.getItems().addAll(EquipmentCondition.values());

        // Show current values of equipment
        initData(equipment);

        this.inventoryService = inventoryService;
        this.selectedEquipment = equipment;
    }

    @FXML
    public void setEquipment(Equipment equipment) {
        this.selectedEquipment = equipment;
        if (equipment != null) {
            equipmentIdField.setText(equipment.getEquipmentId());
            equipmentNameField.setText(equipment.getName());
            statusComboBox.setValue(equipment.getStatus());
            purchaseDateField.setValue(LocalDate.parse(equipment.getPurchaseDate().toString()));
            conditionComboBox.setValue(equipment.getCondition());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedEquipment == null) {
            selectedEquipment = new Equipment();
        }

        // Ensure no field is null
        if (equipmentIdField.getText() == null || equipmentIdField.getText().isEmpty() ||
                equipmentNameField.getText() == null || equipmentNameField.getText().isEmpty() ||
                statusComboBox.getValue() == null ||
                purchaseDateField.getValue() == null ||
                conditionComboBox.getValue() == null) {

            showAlert("Error", "All fields must be filled.");
            return;
        }

        selectedEquipment.setEquipmentId(equipmentIdField.getText());
        selectedEquipment.setName(equipmentNameField.getText());
        selectedEquipment.setStatus(statusComboBox.getValue());
        selectedEquipment.setPurchaseDate(Date.from(purchaseDateField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        selectedEquipment.setCondition(conditionComboBox.getValue());

        try {
            inventoryService.updateEquipment(selectedEquipment);
            closeWindow();
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEquipment != null) {
            inventoryService.deleteById(selectedEquipment.getEquipmentId());
            closeWindow();
        }
    }
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) equipmentIdField.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initData(Equipment equipment) {
        if (equipment != null) {
            equipmentIdField.setText(equipment.getEquipmentId());
            equipmentNameField.setText(equipment.getName());
            statusComboBox.setValue(equipment.getStatus());
            conditionComboBox.setValue(equipment.getCondition());

            if (equipment.getPurchaseDate() != null) {
                Instant instant = equipment.getPurchaseDate().toInstant();
                LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                purchaseDateField.setValue(localDate);
            }
        }
    }

}
