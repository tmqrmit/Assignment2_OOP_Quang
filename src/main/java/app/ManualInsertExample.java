package app;

import app.model.Equipment;
import app.model.EquipmentImage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ManualInsertExample {

//    public static void main(String[] args) {
//        // Step 1: Get an EntityManager instance (adjust your persistence unit name)
//        EntityManager entityManager = jakarta.persistence.Persistence
//                .createEntityManagerFactory("your-persistence-unit")
//                .createEntityManager();
//
//        // Step 2: Find the Equipment entity for ID "EQ001"
//        String equipmentId = "EQ001"; // Equipment ID we are associating the image with
//        Equipment equipment = entityManager.createQuery(
//                        "SELECT e FROM Equipment e WHERE e.equipmentId = :equipmentId", Equipment.class)
//                .setParameter("equipmentId", equipmentId)
//                .getSingleResult();
//
//        if (equipment == null) {
//            System.out.println("No Equipment found with ID: " + equipmentId);
//            return;
//        }
//
//        // Step 3: Read the JPEG image file into a byte array
//        byte[] imageData;
//        Path imagePath = Paths.get("C:\\Users\\admin\\OneDrive\\Hình ảnh\\razerLaptop.jpeg");
//        try {
//            imageData = Files.readAllBytes(imagePath);
//            System.out.println("Image file read successfully.");
//        } catch (Exception e) {
//            System.out.println("Error reading image file: " + e.getMessage());
//            return;
//        }
//
//        // Step 4: Create an EquipmentImage object
//        EquipmentImage equipmentImage = new EquipmentImage(
//                equipment,      // Reference to the Equipment object with ID "EQ001"
//                imageData,      // Binary image data
//                "razerLaptop.jpeg", // File name of the image
//                "image/jpeg"    // MIME type for JPEG images
//        );
//
//        // Step 5: Persist the EquipmentImage object into the database
//        EntityTransaction transaction = entityManager.getTransaction();
//        try {
//            transaction.begin();
//            entityManager.persist(equipmentImage);
//            transaction.commit();
//            System.out.println("EquipmentImage saved successfully with ID: " + equipmentImage.getImageId());
//        } catch (Exception e) {
//            transaction.rollback();
//            System.out.println("Error saving EquipmentImage: " + e.getMessage());
//        } finally {
//            entityManager.close();
//        }
//    }
}