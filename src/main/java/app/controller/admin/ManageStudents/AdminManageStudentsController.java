package app.controller.admin.ManageStudents;

import app.model.Academic;
import app.model.Student;
import app.service.StudentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdminManageStudentsController {

    @FXML private TextField personIdField;
    @FXML private TextField nameField;

    @FXML private TableView<Student> studentsTableView;
    @FXML private TableColumn<Student, String> personIdColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> DOBColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> phoneColumn;

    private StudentService studentService;
    private final ObservableList<Student> studentData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        this.studentService = new StudentService(entityManager);

        loadStudents();
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
    }

    private void setUpRowDoubleClickHandler() {
        studentsTableView.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Student clickedStudent = row.getItem();
                    handleStudentRowPopup(clickedStudent);
                }
            });
            return row;
        });
    }

    @FXML
    public void loadStudents() {
        String idFilter = personIdField.getText().trim();
        String nameFilter = nameField.getText().trim().toLowerCase();

        List<Student> all = studentService.findAllStudents();

        List<Student> filtered = all.stream()
                .filter(a -> idFilter.isEmpty() || Objects.toString(a.getPersonId(), "").equalsIgnoreCase(idFilter))
                .filter(a -> nameFilter.isEmpty() || Objects.toString(a.getFullName(), "").toLowerCase().contains(nameFilter))
                .collect(Collectors.toList());

        studentData.setAll(filtered);
        studentsTableView.setItems(studentData);
    }

    @FXML
    public void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageStudents/admin_add_student.fxml"));
            Parent root = loader.load();

            AdminAddStudentController controller = loader.getController();
            controller.initialize(studentService);

            Stage popupStage = new Stage();
            popupStage.setTitle("Add Student");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            loadStudents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStudentRowPopup(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageStudents/student_row_popup.fxml"));
            Parent root = loader.load();

            StudentRowPopupController controller = loader.getController();
            controller.initialize(studentService, student);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Student");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            loadStudents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCloseWindow() {
        ((Stage) studentsTableView.getScene().getWindow()).close();
    }
}
