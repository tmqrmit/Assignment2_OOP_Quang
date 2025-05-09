package app.controller.admin.ManageAcademics;

import app.model.Academic;
import app.service.AcademicService;
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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdminManageAcademicsController {

    @FXML private TextField personIdField;
    @FXML private TextField nameField;
    @FXML private TextField expertiseField;

    @FXML private TableView<Academic> academicsTableView;
    @FXML private TableColumn<Academic, String> personIdColumn;
    @FXML private TableColumn<Academic, String> nameColumn;
    @FXML private TableColumn<Academic, String> DOBColumn;
    @FXML private TableColumn<Academic, String> emailColumn;
    @FXML private TableColumn<Academic, String> phoneColumn;
    @FXML private TableColumn<Academic, String> expertiseColumn;

    private AcademicService academicService;
    private final ObservableList<Academic> academicData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        this.academicService = new AcademicService(entityManager);

        loadAcademics();
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
        expertiseColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getExpertise()));
    }

    private void setUpRowDoubleClickHandler() {
        academicsTableView.setRowFactory(tv -> {
            TableRow<Academic> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Academic clickedAcademic = row.getItem();
                    handleAcademicRowPopup(clickedAcademic);
                }
            });
            return row;
        });
    }

    @FXML
    public void loadAcademics() {
        String idFilter = personIdField.getText().trim();
        String nameFilter = nameField.getText().trim().toLowerCase();
        String expertiseFilter = expertiseField.getText().trim().toLowerCase();

        List<Academic> all = academicService.findAll();

        List<Academic> filtered = all.stream()
                .filter(a -> idFilter.isEmpty() || Objects.toString(a.getPersonId(), "").equalsIgnoreCase(idFilter))
                .filter(a -> nameFilter.isEmpty() || Objects.toString(a.getFullName(), "").toLowerCase().contains(nameFilter))
                .filter(a -> expertiseFilter.isEmpty() || Objects.toString(a.getExpertise(), "").toLowerCase().contains(expertiseFilter))
                .collect(Collectors.toList());

        academicData.setAll(filtered);
        academicsTableView.setItems(academicData);
    }

    @FXML
    public void handleAddAcademic() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageAcademics/admin_add_academic.fxml"));
            Parent root = loader.load();

            AdminAddAcademicController controller = loader.getController();
            controller.initialize(academicService);

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Academic");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            loadAcademics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAcademicRowPopup(Academic academic) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageAcademics/academic_row_popup.fxml"));
            Parent root = loader.load();

            AcademicRowPopupController controller = loader.getController();
            controller.initialize(academicService, academic);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Academic");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            loadAcademics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCloseWindow() {
        ((Stage) academicsTableView.getScene().getWindow()).close();
    }
}
