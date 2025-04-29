package app.model;

import app.model.enums.LendingRecordStatus;
import app.model.enums.approvalStatus;
import app.service.LendingService;
import app.util.StringSetConverter;
import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "lending_records")
public class LendingRecord {

    @Id
    @Column(name = "record_id", nullable = false, unique = true)
    private String recordId;

    @Column(name = "borrower_id", nullable = false)
    private String borrowerId;

    @Column(name = "borrowed_equipment", columnDefinition = "TEXT")
    @Convert(converter = StringSetConverter.class)
    private Set<String> borrowedEquipment = new HashSet<>();

    @Column(name = "responsible_academic")
    private String responsibleAcademic;

    @Temporal(TemporalType.DATE)
    @Column(name = "borrow_date", nullable = false)
    private Date borrowDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "return_date")
    private Date returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LendingRecordStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "approvalstatus", nullable = false)
    private approvalStatus approval_status;

    @Column(name = "purpose", length = 255)
    private String purpose;

    // Default constructor for JPA
    protected LendingRecord() {}

    // Constructor for initialization
    public LendingRecord(String recordId, String borrowerId, Set<String> borrowedEquipment, String responsibleAcademic, Date borrowDate, Date returnDate, LendingRecordStatus status, String purpose) {
        this.recordId = (recordId == null) ? LendingService.generateRecordId() : recordId;
        this.borrowerId = borrowerId;
        this.borrowedEquipment = borrowedEquipment;
        this.responsibleAcademic = responsibleAcademic;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
        this.purpose = purpose;
        this.approval_status = approvalStatus.PENDING;
    }

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public String getBorrower() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public Set<String> getBorrowedEquipment() {
        return borrowedEquipment;
    }

    public void setBorrowedEquipment(Set<String> borrowedEquipment) {
        this.borrowedEquipment = borrowedEquipment;
    }

    public String getResponsibleAcademic() {
        return responsibleAcademic;
    }

    public void setResponsibleAcademic(String responsibleAcademic) {
        this.responsibleAcademic = responsibleAcademic;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public LendingRecordStatus getStatus() {
        return status;
    }

    public void setStatus(LendingRecordStatus status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public approvalStatus getApprovalStatus() {
        return approval_status;
    }

    public void setApprovalStatus(approvalStatus approvalStatus) {
        this.approval_status = approvalStatus;
    }

    // Utility Methods
    public boolean isOverdue() {
        return returnDate != null && returnDate.before(new Date()) && status == LendingRecordStatus.BORROWED;
    }

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (borrowDate != null && returnDate != null) {
            if (borrowDate.after(returnDate)) {
                throw new IllegalArgumentException("BorrowDate cannot be after ReturnDate.");
            }

            long millisecondsDifference = returnDate.getTime() - borrowDate.getTime();
            long daysDifference = millisecondsDifference / (1000 * 60 * 60 * 24);

            if (daysDifference > 14) {
                throw new IllegalArgumentException("ReturnDate cannot be more than 2 weeks after BorrowDate.");
            }
        }
    }


    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LendingRecord that = (LendingRecord) o;
        return Objects.equals(recordId, that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }
}