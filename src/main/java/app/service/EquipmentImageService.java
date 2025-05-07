package app.service;

import app.model.EquipmentImage;

import jakarta.persistence.EntityManager;
import java.util.Base64;

import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

/**
 * Service class for handling operations related to EquipmentImage.
 */
@Transactional
public class EquipmentImageService {
    private final EntityManager entityManager;

    /**
     * Constructor for EquipmentImageService.
     *
     * @param entityManager The EntityManager instance used for database operations.
     */
    public EquipmentImageService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Save a new EquipmentImage to the database.
     *
     * Ensures that no duplicate image exists for the same equipment.
     *
     * @param equipmentImage The EquipmentImage to be saved.
     * @throws IllegalArgumentException if an image already exists for the given equipmentId.
     */
    public void saveEquipmentImage(EquipmentImage equipmentImage) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Check if an image already exists for the given equipmentId
            EquipmentImage existingImage = findByEquipmentId(equipmentImage.getEquipment().getEquipmentId());

            if (existingImage != null) {
                // If an image exists, delete the old one
                entityManager.remove(existingImage);
            }

            // Persist the new image (either new or replacement)
            entityManager.persist(equipmentImage);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e; // Re-throw to let caller handle or log
        }
    }

    /**
     * Find an EquipmentImage by the associated equipmentId.
     *
     * @param equipmentId The ID of the equipment whose image you want to retrieve.
     * @return The EquipmentImage object if found, or null if no image exists for the equipment.
     */
    public EquipmentImage findByEquipmentId(String equipmentId) {
        try {
            return entityManager.createQuery(
                    "SELECT ei FROM EquipmentImage ei WHERE ei.equipment.equipmentId = :equipmentId", EquipmentImage.class
            ).setParameter("equipmentId", equipmentId).getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        }
    }

    /**
     * Retrieve and display an image for the given equipmentId.
     *
     * Converts the image to Base64 for displaying on web or prints its details in case of a console output.
     *
     * @param equipmentId The ID of the equipment whose image you are trying to display.
     * @return The Base64 string or a success message for display.
     */
    public String retrieveAndDisplayImage(String equipmentId) {
        // Find image by equipmentId
        EquipmentImage image = findByEquipmentId(equipmentId);

        if (image == null) {
            System.out.println("No image found for equipmentId: " + equipmentId);
            return "No image available";
        }

        // Display details (printing image data into readable outputs)
        System.out.println("Image Details:");
        System.out.println("Equipment ID: " + equipmentId);
        System.out.println("File Name: " + image.getFileName());
        System.out.println("File Type: " + image.getFileType());

        // Convert image to Base64 for display
        String base64Image = "data:" + image.getFileType() + ";base64," +
                Base64.getEncoder().encodeToString(image.getImageData());

        // If the system outputs Base64 for web display
        System.out.println("Base64 Image Data:");
        System.out.println(base64Image);

        return base64Image; // Return Base64 string if the caller needs to display it in a web page
    }
}