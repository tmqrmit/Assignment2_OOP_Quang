package app.controller.academic;

import app.model.AppUser;
import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class AcademicStatsController {

    @FXML
    private Label totalItemsLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label overdueItemsLabel;

    @FXML
    private Label borrowFrequencyLabel;

    private CourseService courseService;
    private LendingService lendingService;
    private AppUser appUser;

    // This method initializes the controller with appUser
    @FXML
    public void initialize(AppUser appUser) {
        this.appUser = appUser;
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();

        InventoryService inventoryService = new InventoryService(entityManager);
        StudentService studentService = new StudentService(entityManager);
        AcademicService academicService = new AcademicService(entityManager);
        CourseService courseService = new CourseService(entityManager);

        this.courseService = courseService;
        this.lendingService = new LendingService(entityManager, inventoryService, studentService, academicService, courseService);

        // Load statistics data
        this.loadStatistics();
    }

    @FXML
    private void loadStatistics() {
        // Fetch the lending records for the academic user
        List<LendingRecord> records = lendingService.filterLendingRecords(
                null,
                this.appUser.getPersonId(),
                null, null,
                null, null,
                null, approvalStatus.APPROVED
        );

        // Calculate total borrowed items, overdue items, and borrowing frequency
        int totalBorrowed = 0;
        int totalOverdueItems = 0;
        double totalFrequency = 0;
        int totalCoursesTaught = (int) courseService.getCourseCountByAcademicId(appUser.getPersonId());

        // Loop through records and add number of items borrowed
        for (LendingRecord record : records) {
            totalBorrowed += record.getBorrowedEquipment().size();
            if (record.getStatus().equals(LendingRecordStatus.OVERDUE)) {
                totalOverdueItems += record.getBorrowedEquipment().size();
            }
        }

        if (totalCoursesTaught > 0) totalFrequency = (double) totalBorrowed / totalCoursesTaught;

        // Update the labels with the calculated statistics
        totalItemsLabel.setText(String.valueOf(totalBorrowed));
        totalCoursesLabel.setText(String.valueOf(totalCoursesTaught));
        overdueItemsLabel.setText(String.valueOf(totalOverdueItems));
        borrowFrequencyLabel.setText(String.format("Avg: %.2f", totalFrequency));
    }
}
