package app;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.AcademicService;
import app.service.InventoryService;
import app.service.LendingService;

import app.service.StudentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Test {

//    public static void main(String[] args) {
//        // Initialize required services
//        EntityManager entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
//        InventoryService inventoryService = new InventoryService(entityManager);
//        StudentService studentService = new StudentService(entityManager);
//        AcademicService academicService = new AcademicService(entityManager);
//
//        LendingService lendingService = new LendingService(
//                entityManager,
//                inventoryService,
//                studentService,
//                academicService
//        );
//
//        // Start a new transaction
//        entityManager.getTransaction().begin();
//        try {
//            // Example: Find a lending record by ID
//            LendingRecord record = lendingService.findByRecordId("LR001");
//
//            // Approve the lending record
//            lendingService.returnLendingRecord(record);
//
//            // Commit the transaction to save changes to the database
//            entityManager.getTransaction().commit();
//
//            System.out.println("Lending Record Returned Successfully");
//
//        } catch (Exception e) {
//            // Rollback the transaction in case of an error
//            if (entityManager.getTransaction().isActive()) {
//                entityManager.getTransaction().rollback();
//            }
//            // Print the error
//            System.err.println("Error during transaction: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            // Cleanup and close the EntityManager
//            entityManager.close();
//        }
//    }
//
//    // Helper method to parse date strings
//    private static Date parseDate(String dateString) {
//        try {
//            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid date format: " + dateString, e);
//        }
//    }
}