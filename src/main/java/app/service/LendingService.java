package app.service;

import app.model.Academic;
import app.model.Equipment;
import app.model.LendingRecord;
import app.model.Student;
import app.model.enums.EquipmentStatus;
import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class LendingService {

    private final EntityManager entityManager;
    private final InventoryService inventoryService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final CourseService courseService;

    /**
     * Constructor for custom dependencies.
     *
     * @param entityManager    The EntityManager instance
     * @param inventoryService The InventoryService instance
     */
    public LendingService(EntityManager entityManager, InventoryService inventoryService, StudentService studentService, AcademicService academicService, CourseService courseService) {
        this.entityManager = entityManager;
        this.inventoryService = inventoryService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.courseService = courseService;
    }

    public LendingService() {
        // Initialize the necessary dependencies manually
        this.entityManager = Persistence.createEntityManagerFactory("your-persistence-unit").createEntityManager();
        this.inventoryService = new InventoryService(entityManager);
        this.studentService = new StudentService(entityManager);
        this.academicService = new AcademicService(entityManager);
        this.courseService = new CourseService(entityManager);
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

    /**
     * Find by ID
     */
    public LendingRecord findByRecordId(String recordId) {
        return entityManager.createQuery(
                        "SELECT l FROM LendingRecord l WHERE l.recordId = :recordId", LendingRecord.class)
                .setParameter("recordId", recordId)
                .getSingleResult();
    }

    /**
     * Get number of unique borrower
     */

    public int getUniqueBorrower() {
        return entityManager.createQuery(
                        "SELECT COUNT(DISTINCT l.borrowerId) FROM LendingRecord l", Long.class)
                .getSingleResult().intValue();
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

    /**
     * Checks all lending records and updates their status to OVERDUE if they are overdue.
     */
    public void checkOverdueRecords() {
        List<LendingRecord> allRecords = findAllLendingRecords();

        entityManager.getTransaction().begin();
        for (LendingRecord record : allRecords) {
            if (record.isOverdue() && record.getApprovalStatus() == approvalStatus.APPROVED) {
                entityManager.merge(record);
            }
        }
        entityManager.getTransaction().commit();
    }


    // ================= Methods to Save and Update LendingRecords =================

    /**
     * Save a LendingRecord to the database.
     *
     * @param lendingRecord The LendingRecord to save.
     */
    public void saveRecord(LendingRecord lendingRecord) {
        try {
            // Begin the transaction
            entityManager.getTransaction().begin();

            // Persist the record
            entityManager.persist(lendingRecord);

            // Commit the transaction
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // Rollback the transaction in case of failure
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            // Optionally rethrow or handle the exception
            throw e;
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
        boolean isNewTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                isNewTransaction = true;
            }

            LendingRecord updatedRecord = entityManager.merge(lendingRecord);

            if (isNewTransaction) {
                transaction.commit();
            }

            return updatedRecord;

        } catch (Exception e) {
            if (isNewTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Remove lending record
     */

    public void removeLendingRecord(LendingRecord lendingRecord) {
        try {
            // Begin the transaction
            entityManager.getTransaction().begin();

            // Persist the record
            entityManager.remove(lendingRecord);

            // Commit the transaction
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // Rollback the transaction in case of failure
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            // Optionally rethrow or handle the exception
            throw e;
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

    /**
     * Validates the borrowing and return dates and checks whether the borrower is a student
     * to limit the borrowing period to 2 weeks.
     *
     * @param borrowerId The ID of the person borrowing the equipment.
     * @param borrowDate The date the equipment is being borrowed.
     * @param returnDate The proposed return date for the equipment.
     * @return true if the dates are valid, false otherwise.
     */
    public boolean validateDates(String borrowerId, Date borrowDate, Date returnDate) {
        // Ensure the dates are both provided
        if (borrowDate != null && returnDate != null) {
            // Check if the borrowing date is after the return date
            if (borrowDate.after(returnDate)) {
//                throw new IllegalArgumentException("The borrow date cannot be after the return date.");
                return false;
            }

            // Calculate the difference in days between the borrow and return date
            long millisecondsDifference = returnDate.getTime() - borrowDate.getTime();
            long daysDifference = millisecondsDifference / (1000 * 60 * 60 * 24);

            if (daysDifference > 14) {
                // Reject if a student is borrowing for more than 14 days
//                throw new IllegalArgumentException("The student is currently borrowing for more than 14 days. Please try again later.");
                return false;
            }
        }

        // Return true if all validations are passed
        return true;
    }

    /**
     * Validates the equipment for a lending record.
     * Ensures all equipment:
     * - Exists in the inventory.
     * - Has a valid status (e.g., AVAILABLE for borrowing).
     *
     * @param equipmentIds The list of equipment IDs to validate.
     * @return true if all equipment is valid, otherwise throws an exception.
     * @throws IllegalArgumentException if any equipment does not exist or is not available for lending.
     */
    public boolean validateEquipment(Set<String> equipmentIds) {
        // Ensure the equipment list is not empty
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new IllegalArgumentException("The equipment list cannot be empty.");
        }

        for (String equipmentId : equipmentIds) {
            // Fetch the equipment by ID from the inventory
            Equipment equipment = inventoryService.findByEquipmentId(equipmentId);

            // Check if the equipment exists
            if (equipment == null) {
                throw new IllegalArgumentException("Equipment with ID " + equipmentId + " does not exist in the inventory.");
            }

            // Check if the equipment is available for lending
            if (equipment.getStatus() != EquipmentStatus.AVAILABLE) {
                throw new IllegalArgumentException("Equipment with ID " + equipmentId + " is not available for lending. Current status: " + equipment.getStatus());
            }
        }

        // If all checks pass, return true
        return true;
    }


    /**
     * Approves a lending record with additional logic to handle students and academics.
     * For students:
     * - Adds the lending record to the student's record.
     * - Adds the student to the academic's supervised set.
     * - Sets all associated equipment to BORROWED.
     *
     * @param lendingRecord The LendingRecord to be approved.
     * @return The LendingRecord after approval.
     */
    public LendingRecord approveLendingRecord(LendingRecord lendingRecord) {
        EntityTransaction transaction = entityManager.getTransaction();
        boolean isNewTransaction = false;

        try {
            if (!transaction.isActive()) {
                transaction.begin();
                isNewTransaction = true;
            }

            // Validate equipment
            validateEquipment(lendingRecord.getBorrowedEquipment());

            String borrowerId = lendingRecord.getBorrower();
            Student student = studentService.findByPersonId(borrowerId);

            if (student != null) {
                if (!courseService.hasCourseRelatingStudentToAcademic(student.getPersonId(), lendingRecord.getResponsibleAcademic())) {
                    throw new IllegalArgumentException("The student is not enrolled in a course related to the academic.");
                }

                validateDates(borrowerId, lendingRecord.getBorrowDate(), lendingRecord.getReturnDate());

                student.addLendingRecord(lendingRecord.getRecordId());

                Academic supervisor = academicService.findByPersonId(lendingRecord.getResponsibleAcademic());
                if (supervisor != null) {
                    supervisor.superviseStudent(student.getStudentId());
                    academicService.updateAcademic(supervisor);
                }
            }

            setAllEquipmentToBorrowed(lendingRecord.getBorrowedEquipment());

            lendingRecord.setApprovalStatus(approvalStatus.APPROVED);
            LendingRecord updatedRecord = updateLendingRecord(lendingRecord);

            if (isNewTransaction) {
                transaction.commit();
            }

            return updatedRecord;

        } catch (Exception e) {
            if (isNewTransaction && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    /**
     * Filters lending records based on various parameters like borrower ID, academic, borrow date range, return date range, status,
     * and approval status. All parameters are optional. If a parameter is null, it will be ignored in the filter.
     *
     * @param borrowerId          The ID of the borrower (optional).
     * @param responsibleAcademic The responsible academic (optional).
     * @param borrowDateStart     The start of the borrow date range (optional).
     * @param borrowDateEnd       The end of the borrow date range (optional).
     * @param returnDateStart     The start of the return date range (optional).
     * @param returnDateEnd       The end of the return date range (optional).
     * @param status              The LendingRecordStatus to filter by (optional).
     * @param approvalStatus      The approval status to filter by (optional).
     * @return A list of LendingRecords that match the criteria.
     */
    public List<LendingRecord> filterLendingRecords(
            String borrowerId,
            String responsibleAcademic,
            Date borrowDateStart,
            Date borrowDateEnd,
            Date returnDateStart,
            Date returnDateEnd,
            LendingRecordStatus status,
            approvalStatus approvalStatus
    ) {
        // Begin building the JPQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT lr FROM LendingRecord lr WHERE 1=1");

        // Append conditions dynamically based on parameters
        if (borrowerId != null && !borrowerId.isEmpty()) {
            queryBuilder.append(" AND lr.borrowerId = :borrowerId");
        }
        if (responsibleAcademic != null && !responsibleAcademic.isEmpty()) {
            queryBuilder.append(" AND lr.responsibleAcademic = :responsibleAcademic");
        }
        if (borrowDateStart != null) {
            queryBuilder.append(" AND lr.borrowDate >= :borrowDateStart");
        }
        if (borrowDateEnd != null) {
            queryBuilder.append(" AND lr.borrowDate <= :borrowDateEnd");
        }
        if (returnDateStart != null) {
            queryBuilder.append(" AND lr.returnDate >= :returnDateStart");
        }
        if (returnDateEnd != null) {
            queryBuilder.append(" AND lr.returnDate <= :returnDateEnd");
        }
        if (status != null) {
            queryBuilder.append(" AND lr.status = :status");
        }
        if (approvalStatus != null) {
            queryBuilder.append(" AND lr.approval_status = :approvalStatus");
        }

        // Create a query from the dynamically built JPQL
        var query = entityManager.createQuery(queryBuilder.toString(), LendingRecord.class);

        // Set parameters dynamically
        if (borrowerId != null && !borrowerId.isEmpty()) {
            query.setParameter("borrowerId", borrowerId);
        }
        if (responsibleAcademic != null && !responsibleAcademic.isEmpty()) {
            query.setParameter("responsibleAcademic", responsibleAcademic);
        }
        if (borrowDateStart != null) {
            query.setParameter("borrowDateStart", borrowDateStart);
        }
        if (borrowDateEnd != null) {
            query.setParameter("borrowDateEnd", borrowDateEnd);
        }
        if (returnDateStart != null) {
            query.setParameter("returnDateStart", returnDateStart);
        }
        if (returnDateEnd != null) {
            query.setParameter("returnDateEnd", returnDateEnd);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (approvalStatus != null) {
            query.setParameter("approvalStatus", approvalStatus);
        }

        // Execute the query and return the results
        return query.getResultList();
    }

    /**
     * Handles the return of an approved lending record.
     * Assumes that transaction management is handled externally.
     *
     * @param lendingRecord The lending record to be returned.
     * @throws IllegalArgumentException if the record is invalid (e.g., does not exist, isn't borrowed, etc.).
     */
    public void returnLendingRecord(LendingRecord lendingRecord) {
        if (lendingRecord.getApprovalStatus() != approvalStatus.APPROVED) {
            throw new IllegalArgumentException("Lending record with ID " + lendingRecord.getRecordId() + " is not approved. Status: " + lendingRecord.getApprovalStatus());
        }
        if (lendingRecord == null) {
            throw new IllegalArgumentException(" Record should not be null");
        }

        if (lendingRecord.getStatus() == LendingRecordStatus.RETURNED) {
            throw new IllegalArgumentException("Lending record with ID " + lendingRecord.getRecordId() + " has already been returned.");
        }
        // Handle if borrower is a student
        Student student = studentService.findByPersonId(lendingRecord.getBorrower());
        if (student != null) {
            Academic supervisor = academicService.findByPersonId(lendingRecord.getResponsibleAcademic());
            List<LendingRecord> ongoingRecords = filterLendingRecords(student.getPersonId(), supervisor.getPersonId(), null, null, null, null, LendingRecordStatus.BORROWED, approvalStatus.APPROVED);
            if (ongoingRecords.size() <= 1) {
                supervisor.stopSupervisingStudent(student.getStudentId());
            }
            academicService.updateAcademic(supervisor);
        }

        // Check the associated equipment
        Set<String> equipmentIds = lendingRecord.getBorrowedEquipment();
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new IllegalArgumentException("Lending record with ID " + lendingRecord.getRecordId() + " has no associated equipment.");
        }

        // Update the status of all associated equipment to AVAILABLE
        for (String equipmentId : equipmentIds) {
            Equipment equipment = inventoryService.findByEquipmentId(equipmentId);
            if (equipment.getStatus() != EquipmentStatus.BORROWED) {
                throw new IllegalArgumentException("Equipment with ID " + equipment.getEquipmentId() + " is in an invalid status: " + equipment.getStatus());
            }
            equipment.setStatus(EquipmentStatus.AVAILABLE);
            inventoryService.updateEquipment(equipment); // Persist the equipment changes
        }

        // Update the lending record status to RETURNED
        lendingRecord.setStatus(LendingRecordStatus.RETURNED);
        lendingRecord.setReturnDate(new Date()); // Set return date to current date
        updateLendingRecord(lendingRecord); // Persist the updated lending record

        System.out.println("Lending record with ID " + lendingRecord.getRecordId() + " has been successfully returned.");
    }

    /**
     * Find and return all LendingRecords that are overdue as of today.
     *
     * @return A list of LendingRecords that are overdue for today's date.
     */
    public List<LendingRecord> findOverdueRecordsForToday() {
        List<LendingRecord> allRecords = findAllLendingRecords(); // Get all lending records

        // Filter records using the isOverdue() method
        return allRecords.stream()
                .filter(LendingRecord::isOverdue) // Convenience: Directly checking overdue status
                .collect(Collectors.toList());
    }

    /**
     * Delete all LendingRecords with the status PENDING.
     */
    public void deleteAllPendingRecords() {
        // Create a query to delete all PENDING records
        entityManager.createQuery("DELETE FROM LendingRecord lr WHERE lr.approval_status = :status")
                .setParameter("status", approvalStatus.PENDING)
                .executeUpdate();
    }

    /**
     * Find most borrowed equipment
     */

    public Equipment findMostBorrowedEquipment() {
        List<LendingRecord> records = filterLendingRecords(null, null, null, null, null, null, null, approvalStatus.APPROVED);

        if (records.isEmpty()) return null;

        // Count each equipment's borrow frequency
        Map<String, Long> frequencyMap = records.stream()
                .flatMap(record -> record.getBorrowedEquipment().stream()) // Flatten borrowed equipment from each record
                .collect(Collectors.groupingBy(
                        equipmentId -> equipmentId, // group by equipmentId
                        Collectors.counting()       // count occurrences
                ));

        // Find the equipment ID with the max frequency
        String mostBorrowedId = frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue()) // Get the entry with the highest count
                .map(Map.Entry::getKey)           // Get the equipmentId from that entry
                .orElse(null);                    // If no equipment found, return null

        if (mostBorrowedId == null) return null;

        // Return the Equipment entity corresponding to the most borrowed equipment ID
        return inventoryService.findByEquipmentId(mostBorrowedId);
    }

    /**
     * Turn every equipment in the list to BORROWED
     */
    public void setAllEquipmentToBorrowed(Set<String> equipmentList) {
        equipmentList.forEach(equipmentId -> {
            // Find the Equipment object by its ID
            var equipment = inventoryService.findByEquipmentId(equipmentId);
            if (equipment != null) {
                // Set status to BORROWED
                equipment.setStatus(EquipmentStatus.BORROWED);
                inventoryService.updateEquipment(equipment); // Save equipment status
            } else {
                throw new IllegalArgumentException("Equipment with ID " + equipmentId + " not found.");
            }
        });
    }

    /**
     * Get number of times borrowed for an equipment
     */

    public int findTotalTimesBorrowed(Equipment equipment) {
        List<LendingRecord> records = filterLendingRecords(
                null, null, null, null, null, null, null, approvalStatus.APPROVED
        );

        String targetId = equipment.getEquipmentId();
        int count = 0;

        for (LendingRecord lr : records) {
            Set<String> equipmentSet = lr.getBorrowedEquipment();
            if (equipmentSet.contains(targetId)) {
                count++;
            }
        }

        return count;
    }

}

