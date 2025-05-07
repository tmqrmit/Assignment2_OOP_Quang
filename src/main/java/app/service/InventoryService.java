package app.service;

import app.model.Equipment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public class InventoryService {

    private final EntityManager entityManager;

    /**
     * Constructor for InventoryService.
     * Injects an EntityManager directly into the service.
     *
     * @param entityManager the EntityManager used for inventory management
     */
    public InventoryService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Save a new Equipment entity to the inventory.
     *
     * Ensures that no duplicate equipmentId exists before saving.
     *
     * @param equipment the Equipment to be saved
     * @throws IllegalArgumentException if an Equipment with the same equipmentId already exists
     */
    public void saveEquipment(Equipment equipment) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Check for duplicate equipmentId
            Equipment existingEquipment = findByEquipmentId(equipment.getEquipmentId());
            if (existingEquipment != null) {
                throw new IllegalArgumentException("Equipment with equipmentId " + equipment.getEquipmentId() + " already exists.");
            }

            entityManager.persist(equipment);

            transaction.commit(); // Commit if successful
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback(); // Rollback if error occurs
            }
            throw e; // Rethrow the exception to handle it at a higher level
        }
    }

    /**
     * Find an Equipment entity in the inventory by its equipmentId.
     *
     * @param equipmentId the equipmentId of the Equipment to retrieve
     * @return the Equipment entity, or null if not found
     */
    public Equipment findByEquipmentId(String equipmentId) {
        try {
            return entityManager.createQuery(
                    "SELECT e FROM Equipment e WHERE e.equipmentId = :equipmentId", Equipment.class
            ).setParameter("equipmentId", equipmentId).getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        }
    }




    /**
     * Retrieve all Equipment entities from the inventory.
     *
     * @return a List of all Equipment entities
     */
    public List<Equipment> findAll() {
        return entityManager.createQuery("SELECT e FROM Equipment e", Equipment.class).getResultList();
    }

    /**
     * Update an existing Equipment entity in the inventory.
     *
     * Ensures that no duplicate equipmentId exists in another record.
     *
     * @param equipment the Equipment with updated data
     * @return the updated Equipment entity
     * @throws IllegalArgumentException if the updated equipmentId already exists in another record
     */
    public Equipment updateEquipment(Equipment equipment) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                isNewTransaction = true;
            }

            // Check for duplicate equipmentId
            Equipment existingEquipment = findByEquipmentId(equipment.getEquipmentId());
            if (existingEquipment != null && !existingEquipment.getEquipmentId().equals(equipment.getEquipmentId())) {
                throw new IllegalArgumentException("Equipment with equipmentId " + equipment.getEquipmentId() + " already exists.");
            }

            Equipment updatedEquipment = entityManager.merge(equipment);

            if (isNewTransaction) {
                transaction.commit();
            }

            return updatedEquipment;
        } catch (Exception e) {
            if (isNewTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Delete an Equipment entity in the inventory by its equipmentId.
     *
     * @param equipmentId the equipmentId of the Equipment to delete
     */
    public void deleteById(String equipmentId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Equipment equipment = findByEquipmentId(equipmentId);
            if (equipment != null) {
                entityManager.remove(equipment);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }
}