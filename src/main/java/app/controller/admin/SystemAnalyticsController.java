package app.controller.admin;

import app.model.Equipment;
import app.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SystemAnalyticsController {

    @FXML private Label totalLendingLabel;
    @FXML private Label totalEquipmentLabel;
    @FXML private Label totalBorrowersLabel;
    @FXML private Label overdueRateLabel;

    @FXML private Label mostBorrowedNameLabel;
    @FXML private Label mostBorrowedCountLabel;

    @FXML private Label borrowingFrequencyLabel;

    private LendingService lendingService;
    private InventoryService inventoryService;
    private StudentService studentService;
    private AcademicService academicService;

    @FXML
    public void initialize() {
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("your-persistence-unit")
                .createEntityManager();

        this.inventoryService = new InventoryService(entityManager);
        this.studentService = new StudentService(entityManager);
        this.academicService = new AcademicService(entityManager);
        this.lendingService = new LendingService(entityManager, inventoryService, studentService, academicService, null);

        loadAnalytics();
    }

    private void loadAnalytics() {
        // Total lending records
        int totalLending = lendingService.findAllLendingRecords().size();
        totalLendingLabel.setText(String.valueOf(totalLending));

        // Total equipment
        int totalEquipment = inventoryService.findAll().size();
        totalEquipmentLabel.setText(String.valueOf(totalEquipment));

        // Total borrowers (unique users who have borrowed)
        int totalBorrowers = lendingService.getUniqueBorrower();
        totalBorrowersLabel.setText(String.valueOf(totalBorrowers));

        // Overdue rate
        int overdue = lendingService.listOverdueLendingRecords().size();
        double overdueRate = totalLending > 0 ? (overdue * 100.0) / totalLending : 0.0;
        overdueRateLabel.setText(String.format("%.2f%%", overdueRate));

        // Most borrowed equipment
        Equipment mostBorrowed = lendingService.findMostBorrowedEquipment();
        if (mostBorrowed != null) {
            mostBorrowedNameLabel.setText(mostBorrowed.getName());
            mostBorrowedCountLabel.setText(String.valueOf(lendingService.findTotalTimesBorrowed(mostBorrowed)));
        }

        // Borrowing frequency summary
        double avgBorrowingPerUser = totalBorrowers > 0 ? (double) totalLending / totalBorrowers : 0;
        borrowingFrequencyLabel.setText(String.format("Avg/User: %.2f", avgBorrowingPerUser));
    }
}
