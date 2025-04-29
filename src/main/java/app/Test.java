package app;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.InventoryService;
import app.service.LendingService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        // Set up an EntityManagerFactory to simulate a database transaction
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("your-persistence-unit");
        EntityManager entityManager = emf.createEntityManager();
        InventoryService inventoryService = new InventoryService(entityManager); // Stubbed or mock inventory service

        // Instantiate LendingService
        LendingService lendingService = new LendingService(entityManager, inventoryService);

//        Calendar calendar = Calendar.getInstance();
//        Date borrowDate = new Date(); // Today's date
//        calendar.add(Calendar.DAY_OF_YEAR, 7); // Add 7 days for the return date
//        Date returnDate = calendar.getTime(); // Return date
//
//        LendingRecord pendingRecord = new LendingRecord(
//                null, // Let the recordId be auto-generated
//                "borrower_001",
//                Set.of("EQ-001", "EQ-002"),
//                "Dr. John Doe",
//                borrowDate, // Borrow date is today
//                returnDate, // Return date is one week later
//                LendingRecordStatus.BORROWED,
//                "For academic research"
//        );
//
        LendingRecord pendingRecord = lendingService.findAllByApprovalStatus(approvalStatus.PENDING).get(0);

        try {
            // Save the pending LendingRecord
//            lendingService.saveRecord(pendingRecord);
            System.out.println("Record saved with ID: " + pendingRecord.getRecordId());

            LendingRecord retrievedRecord = lendingService.findAllByApprovalStatus(approvalStatus.PENDING).get(0);

            if (retrievedRecord != null) {
                System.out.println("Record retrieved successfully!");
                System.out.println("");
                System.out.println("Approval Status: " + retrievedRecord.getApprovalStatus());
                System.out.println("Retrieved Record ID: " + retrievedRecord.getRecordId());
                System.out.println("Retrieved Borrower ID: " + retrievedRecord.getBorrower());

                // Verification
                if (retrievedRecord.getRecordId().equals(pendingRecord.getRecordId()) &&
                        retrievedRecord.getBorrower().equals(pendingRecord.getBorrower())) {
                    System.out.println("Test Passed: Saved and Retrieved LendingRecords match!");
                } else {
                    System.err.println("Test Failed: Retrieved record does not match saved record.");
                }
            } else {
                System.err.println("Test Failed: No record retrieved.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            entityManager.close();
            emf.close();
        }
    }
}