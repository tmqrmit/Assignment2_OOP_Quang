package app.controller.admin.ManageProfessionals;

import app.model.Academic;
import app.model.Professional;
import app.service.ProfessionalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdminManageProfessionalsController {

    @FXML private TextField personIdField;
    @FXML private TextField nameField;
    @FXML private TextField departmentField;

    @FXML private TableView<Professional> professionalTableView;
    @FXML private TableColumn<Professional, String> personIdColumn;
    @FXML private TableColumn<Professional, String> nameColumn;
    @FXML private TableColumn<Professional, String> DOBColumn;
    @FXML private TableColumn<Professional, String> emailColumn;
    @FXML private TableColumn<Professional, String> phoneColumn;
    @FXML private TableColumn<Professional, String> departmentColumn;

    private ProfessionalService professionalService;
    private final ObservableList<Professional> professionalData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        this.professionalService = new ProfessionalService(entityManager);

        loadProfessionals();
        setupTableColumns();
        setUpRowDoubleClickHandler();
    }

    private void setupTableColumns() {
        personIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersonId()));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));
        DOBColumn.setCellValueFactory(data -> {
            Date dob = data.getValue().getDateOfBirth();
            if (dob != null) {
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(dob);
                return new SimpleStringProperty(formattedDate);
            } else {
                return new SimpleStringProperty("");
            }
        });
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        phoneColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhoneNumber()));
        departmentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartment()));
    }

    private void setUpRowDoubleClickHandler() {
        professionalTableView.setRowFactory(tv -> {
            TableRow<Professional> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Professional clickedProfessional = row.getItem();
                    handleProfessionalRowPopup(clickedProfessional);
                }
            });
            return row;
        });
    }

    @FXML
    public void loadProfessionals() {
        String idFilter = personIdField.getText().trim();
        String nameFilter = nameField.getText().trim().toLowerCase();
        String deptFilter = departmentField.getText().trim().toLowerCase();

        List<Professional> all = professionalService.findAll();

        List<Professional> filtered = all.stream()
                .filter(a -> idFilter.isEmpty() || Objects.toString(a.getPersonId(), "").equalsIgnoreCase(idFilter))
                .filter(a -> nameFilter.isEmpty() || Objects.toString(a.getFullName(), "").toLowerCase().contains(nameFilter))
                .filter(a -> deptFilter.isEmpty() || Objects.toString(a.getDepartment(), "").toLowerCase().contains(deptFilter))
                .collect(Collectors.toList());

        professionalData.setAll(filtered);
        professionalTableView.setItems(professionalData);
    }

    @FXML
    public void handleAddProfessional() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageProfessionals/admin_add_professional.fxml"));
            Parent root = loader.load();

            AdminAddProfessionalController controller = loader.getController();
            controller.initialize(professionalService);

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Professional");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            loadProfessionals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleProfessionalRowPopup(Professional professional) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageProfessionals/professional_row_popup.fxml"));
            Parent root = loader.load();

            ProfessionalRowPopupController controller = loader.getController();
            controller.initialize(professionalService, professional);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Professional");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            loadProfessionals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCloseWindow() {
        ((Stage) professionalTableView.getScene().getWindow()).close();
    }
}
