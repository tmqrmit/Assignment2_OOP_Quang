package app.service;

import app.model.LendingRecord;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.Date;
import java.util.List;

public class LendingService {

    private final EntityManager entityManager;
    private final InventoryService inventoryService;

    /**
     * Constructor for custom dependencies.
     *
     * @param entityManager    The EntityManager instance
     * @param inventoryService The InventoryService instance
     */
    public LendingService(EntityManager entityManager, InventoryService inventoryService) {
        this.entityManager = entityManager;
        this.inventoryService = inventoryService;
    }

    /**
     * Generates a unique record ID for LendingRecord.
     * This method is static for easy reuse without needing a LendingService instance.
     *
     * @return A unique record ID
     */
    public static String generateRecordId() {
        return "LR-" + System.currentTimeMillis();
    }

    // ================= Methods for LendingRecords by ApprovalStatus and Status =================

    /**
     * Find LendingRecords by approval_status and status.
     *
     * @param approvalStatus The approval status of the record(s) to look up.
     * @param status         The lending record status.
     * @return A list of LendingRecords that match the criteria.
     */
    public List<LendingRecord> findByApprovalStatusAndStatus(approvalStatus approvalStatus, LendingRecordStatus status) {
        return entityManager.createQuery(
                        "SELECT l FROM LendingRecord l WHERE l.approval_status = :approvalStatus AND l.status = :status",
                        LendingRecord.class)
                .setParameter("approvalStatus", approvalStatus)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Find all LendingRecords with the specified approval_status.
     *
     * @param approvalStatus The approval status to filter by.
     * @return A list of all LendingRecords with the specified approval status.
     */
    public List<LendingRecord> findAllByApprovalStatus(approvalStatus approvalStatus) {
        return entityManager.createQuery(
                        "SELECT l FROM LendingRecord l WHERE l.approval_status = :approvalStatus", LendingRecord.class)
                .setParameter("approvalStatus", approvalStatus)
                .getResultList();
    }

    // ================= Overdue LendingRecords =================

    /**
     * List all overdue LendingRecords by approval_status.
     *
     * @return List of overdue LendingRecords.
     */
    public List<LendingRecord> listOverdueLendingRecords() {
        return entityManager.createQuery(
                        "SELECT l FROM LendingRecord l WHERE l.status = :status AND l.approval_status = :approvalStatus AND l.returnDate < :currentDate",
                        LendingRecord.class)
                .setParameter("status", LendingRecordStatus.BORROWED)
                .setParameter("approvalStatus", approvalStatus.APPROVED)
                .setParameter("currentDate", new Date())
                .getResultList();
    }

    // ================= Methods to Save and Update LendingRecords =================

    /**
     * Save a LendingRecord to the database.
     *
     * @param lendingRecord The LendingRecord to save.
     */
    public void saveRecord(LendingRecord lendingRecord) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(lendingRecord);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Failed to save lending record: " + e.getMessage(), e);
        }
    }

    /**
     * Approve a pending LendingRecord by changing its approval status.
     *
     * @param lendingRecord The LendingRecord to approve.
     */
    public void approveLendingRecord(LendingRecord lendingRecord) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            lendingRecord.setApprovalStatus(approvalStatus.APPROVED);
            entityManager.merge(lendingRecord);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Failed to approve LendingRecord: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing LendingRecord.
     *
     * @param lendingRecord The LendingRecord to update.
     * @return The updated LendingRecord.
     */
    public LendingRecord updateLendingRecord(LendingRecord lendingRecord) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            LendingRecord updatedRecord = entityManager.merge(lendingRecord);
            transaction.commit();
            return updatedRecord;
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Failed to update lending record: " + e.getMessage(), e);
        }
    }

    // ================= Additional Utility Methods =================

    /**
     * Find all LendingRecords regardless of approval status.
     *
     * @return A list of all LendingRecords.
     */
    public List<LendingRecord> findAllLendingRecords() {
        return entityManager.createQuery(
                        "SELECT l FROM LendingRecord l", LendingRecord.class)
                .getResultList();
    }
}