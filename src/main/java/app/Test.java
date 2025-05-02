package app;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.InventoryService;
import app.service.LendingService;
import app.service.StudentService;
import app.service.AcademicService;
import app.service.CourseService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Test {
//    public static void main(String[] args) {
//        // Step 1: Setup EntityManager and Required Services
//        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
//        InventoryService inventoryService = new InventoryService(entityManager);
//        StudentService studentService = new StudentService(entityManager);
//        AcademicService academicService = new AcademicService(entityManager);
//        CourseService courseService = new CourseService(entityManager);
//
//        LendingService lendingService = new LendingService(
//                entityManager,
//                inventoryService,
//                studentService,
//                academicService,
//                courseService
//        );
//
////        entityManager.getTransaction().begin();
//
////        LendingService lendingService = new LendingService();
//
//
//        try {
//            // Step 3: Create a new LendingRecord
//            LendingRecord lendingRecord = new LendingRecord(
//                    null,
//                    "ST123123123", // Borrower ID
//                    new HashSet<>(List.of("bgfbfb", "bfdbdf")), // Borrowed equipment
//                    "Prof. Alex", // Responsible academic
//                    parseDate("2023-11-01"), // Borrow date
//                    parseDate("2023-11-15"), // Return date
//                    LendingRecordStatus.BORROWED, // Lending record status
//                    "Research" // Purpose
//            );
//            lendingRecord.setApprovalStatus(approvalStatus.PENDING);
//
//            // Step 4: Save the LendingRecord
//            lendingService.saveRecord(lendingRecord);
//
//////            // Step 5: Flush and Commit the Transaction
////            entityManager.flush();
////            entityManager.getTransaction().commit();
//
//            // Step 6: Verify the LendingRecord Was Saved
//            LendingRecord savedRecord = lendingService.findByRecordId(lendingRecord.getRecordId());
//
//            if (savedRecord != null) {
//                // Record saved - Print information
//                System.out.println("LendingRecord saved successfully:");
//                System.out.println("Record ID: " + savedRecord.getRecordId());
//                System.out.println("Borrower ID: " + savedRecord.getBorrower());
//                System.out.println("Purpose: " + savedRecord.getPurpose());
//            } else {
//                // Record not found
//                System.err.println("Error: LendingRecord not saved to the database.");
//            }
//        } catch (Exception e) {
//            // Rollback transaction in case of failure
//            if (entityManager.getTransaction().isActive()) {
//                entityManager.getTransaction().rollback();
//            }
//            System.err.println("Error during transaction: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            // Step 7: Close EntityManager
//            entityManager.close();
//        }
//    }
//
//    // Helper method to parse dates
//    private static Date parseDate(String dateString) {
//        try {
//            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
//        }
//    }
}