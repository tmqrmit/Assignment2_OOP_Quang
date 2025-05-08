package app.controller.admin.ManageCourses;

import app.controller.admin.ManageEquipment.AdminAddEquipmentController;
import app.model.Course;
import app.service.CourseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
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
import java.util.List;
import java.util.stream.Collectors;

public class AdminManageCoursesController {

    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField academicIdField;
    @FXML private TextField studentIdField;

    @FXML private TableView<Course> coursesTableView;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> academicIdColumn;
    @FXML private TableColumn<Course, String> studentIdsColumn;

    private CourseService courseService;
    private final ObservableList<Course> courseData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        this.courseService = new CourseService(entityManager);

        // Set up table
        loadCourses();
        setUpTableColumns();
        setUpRowDoubleClickHandler();
    }

    private void setUpTableColumns() {
        courseIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseId()));
        courseNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseName()));
        academicIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAcademicId()));
        studentIdsColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.join(", ", data.getValue().getStudentIds()))
        );
    }

    private void setUpRowDoubleClickHandler() {
        coursesTableView.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Course clickedCourse = row.getItem();
                    handleCourseRowPopup(clickedCourse);
                }
            });
            return row;
        });
    }


    @FXML
    public void loadCourses() {
        String courseIdFilter = courseIdField.getText().trim();
        String courseNameFilter = courseNameField.getText().trim().toLowerCase();
        String academicIdFilter = academicIdField.getText().trim();
        String studentIdFilter = studentIdField.getText().trim();

        List<Course> allCourses = courseService.findAllCourses();

        List<Course> filtered = allCourses.stream()
                .filter(c -> courseIdFilter.isEmpty() || c.getCourseId().equalsIgnoreCase(courseIdFilter))
                .filter(c -> courseNameFilter.isEmpty() || c.getCourseName().toLowerCase().contains(courseNameFilter))
                .filter(c -> academicIdFilter.isEmpty() || c.getAcademicId().equalsIgnoreCase(academicIdFilter))
                .filter(c -> studentIdFilter.isEmpty() || c.getStudentIds().contains(studentIdFilter))
                .collect(Collectors.toList());

        courseData.setAll(filtered);
        coursesTableView.setItems(courseData);
    }

    @FXML
    public void handleAddCourse() {
        try {
            // Load the FXML for the popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageCourses/admin_add_course.fxml"));
            Parent root = loader.load();

            // Get the popup controller and initialize it with necessary data
            AdminAddCourseController controller = loader.getController();

            controller.initialize(courseService);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Equipment");

            // Set the scene for the popup stage
            popupStage.setScene(new Scene(root, 400, 400));
            popupStage.initModality(Modality.APPLICATION_MODAL); // Set modality to block interaction with other windows

            // Show the popup and wait until it's closed
            popupStage.showAndWait();

            // Refresh table
            loadCourses();

        } catch (IOException e) {
            // Log or display the error if FXML or other initialization fails
            e.printStackTrace();
        }

    }

    @FXML
    private void handleCourseRowPopup(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/manageCourses/course_row_popup.fxml"));
            Parent root = loader.load();

            CourseRowPopupController controller = loader.getController();
            controller.initialize(courseService, course);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Course");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            loadCourses(); // Refresh table after edit
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCloseWindow() {
        ((javafx.stage.Stage) coursesTableView.getScene().getWindow()).close();
    }
}
