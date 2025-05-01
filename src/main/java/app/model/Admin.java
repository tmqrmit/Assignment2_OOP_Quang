package app.model;

import app.model.Equipment;
import app.model.EquipmentImage;
import app.model.enums.approvalStatus;
import app.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Admin class provides administrators with centralized access
 * to all service functionalities to perform CRUD operations on
 * any entities, manage equipment, and handle images.
 */
public class Admin {

    private final InventoryService inventoryService;
    private final EquipmentImageService equipmentImageService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final ProfessionalService professionalService;
    private final AppUserService appUserService;
    private final LendingService lendingService;

    /**
     * Constructor for Admin.
     * Initializes all service dependencies.
     *
     * @param inventoryService       service for managing equipment inventory
     * @param equipmentImageService  service for managing equipment images
     * @param courseService          service for managing courses
     * @param studentService         service for managing students
     * @param academicService        service for managing academics
     * @param appUserService         service for managing application users
     * @param lendingService         service for managing lending records
     */
    public Admin(InventoryService inventoryService,
                 EquipmentImageService equipmentImageService,
                 CourseService courseService,
                 StudentService studentService,
                 AcademicService academicService,
                 ProfessionalService professionalService,
                 AppUserService appUserService,
                 LendingService lendingService) {
        this.inventoryService = inventoryService;
        this.equipmentImageService = equipmentImageService;
        this.courseService = courseService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.professionalService = professionalService;
        this.appUserService = appUserService;
        this.lendingService = lendingService;
    }


    /**
     * Upload an image for a piece of equipment using EquipmentImageService.
     *
     * @param equipmentId the ID of the equipment
     * @param imageData   the binary content of the image
     * @param fileName    the name of the file
     * @param fileType    the type of the file (e.g. "image/png")
     */
    public void uploadEquipmentImage(String equipmentId, byte[] imageData, String fileName, String fileType) {
        Equipment equipment = inventoryService.findByEquipmentId(equipmentId);

        if (equipment == null) {
            throw new IllegalArgumentException("Equipment with ID: " + equipmentId + " not found.");
        }

        EquipmentImage equipmentImage = new EquipmentImage(equipment, imageData, fileName, fileType);
        equipmentImageService.saveEquipmentImage(equipmentImage);
    }

    // ======================== STATISTICS OPERATIONS ========================

    /**
     * Get total counts of important entities in the system, such as:
     * - Total number of equipment
     * - Total number of borrowers (students + academics)
     * - Total number of lending records
     *
     * @return a map summarizing these system-level statistics
     */
    public Map<String, Integer> getSystemStatistics() {
        int totalEquipment = inventoryService.findAll().size();
        int totalStudents = studentService.findAllStudents().size();
        int totalAcademics = academicService.findAll().size();
        int totalProfessionals = professionalService.findAll().size();
        int totalLendingRecords = lendingService.findAllLendingRecords().size();

        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("Total Equipment", totalEquipment);
        statistics.put("Total Students", totalStudents);
        statistics.put("Total Academics", totalAcademics);
        statistics.put("Total Professionals", totalProfessionals);
        statistics.put("Total Lending Records", totalLendingRecords);

        return statistics;
    }

    /**
     * Find the most borrowed equipment in the system.
     * Use a Map to count lending records per equipment and find the maximum.
     *
     * @return the Equipment that has been borrowed the most
     */
    public Equipment getMostBorrowedEquipment() {
        List<LendingRecord> lendingRecords = lendingService.filterLendingRecords(null, null, null, null, null, null, null, approvalStatus.APPROVED);

        // Count borrowings per equipment ID
        Map<String, Long> equipmentBorrowCount = lendingRecords.stream()
                .flatMap(record -> record.getBorrowedEquipment().stream())
                .collect(Collectors.groupingBy(equipmentId -> equipmentId, Collectors.counting()));

        // Find the equipment ID with the maximum borrow count
        Optional<Map.Entry<String, Long>> mostBorrowed = equipmentBorrowCount.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        return mostBorrowed
                .map(entry -> inventoryService.findByEquipmentId(entry.getKey()))
                .orElse(null); // Return null if no records found
    }

    /**
     * Get the percentage of overdue lending records from the total records.
     *
     * @return the overdue rate as a percentage
     */
    public double getOverdueRate() {
        List<LendingRecord> lendingRecords = lendingService.filterLendingRecords(null, null, null, null, null, null, null, approvalStatus.APPROVED);
        long overdueCount = lendingRecords.stream()
                .filter(LendingRecord::isOverdue) // Assuming LendingRecord has an `isOverdue` method
                .count();

        if (lendingRecords.isEmpty()) {
            return 0.0; // Avoid division by zero
        }

        return (overdueCount / (double) lendingRecords.size()) * 100;
    }

    /**
     * Get usage trends by calculating how often each piece of equipment is used.
     *
     * @return a sorted map of equipment IDs to borrow counts (descending order of usage)
     */
    public Map<String, Long> getEquipmentUsageTrends() {
        List<LendingRecord> lendingRecords = lendingService.findAllLendingRecords();

        // Count borrowings per equipment ID
        Map<String, Long> equipmentUsage = lendingRecords.stream()
                .flatMap(record -> record.getBorrowedEquipment().stream())
                .collect(Collectors.groupingBy(equipmentId -> equipmentId, Collectors.counting()));

        // Sort the map by usage count in descending order
        return equipmentUsage.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}
