package app.model;

import app.model.enums.EquipmentCondition;
import app.model.enums.EquipmentStatus;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
public class Equipment {
    @Id
    @Column(nullable = false, unique = true)
    private String equipmentId;

    private String name;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    @Temporal(TemporalType.DATE)
    private Date purchaseDate;

    @Enumerated(EnumType.STRING)
    private EquipmentCondition condition;

    public Equipment() {
        // Required by JPA
    }

    public Equipment(String equipmentId, String name, EquipmentStatus status, Date purchaseDate, EquipmentCondition condition) {
        if (equipmentId == null || equipmentId.isEmpty())
            throw new IllegalArgumentException("Equipment ID cannot be null or empty.");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Equipment name cannot be null or empty.");
        if (purchaseDate == null)
            throw new IllegalArgumentException("Purchase date cannot be null.");

        this.equipmentId = equipmentId;
        this.name = name;
        this.status = status;
        this.purchaseDate = new Date(purchaseDate.getTime());
        this.condition = condition;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }
    public String getEquipmentId() {
        return equipmentId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public Date getPurchaseDate() {
        return (purchaseDate != null) ? new Date(purchaseDate.getTime()) : null;
    }
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = (purchaseDate != null) ? new Date(purchaseDate.getTime()) : null;
    }
    public EquipmentCondition getCondition() {
        return condition;
    }

    public void setCondition(EquipmentCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Equipment equipment)) return false;
        return Objects.equals(equipmentId, equipment.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId);
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "equipmentId='" + equipmentId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", purchaseDate=" + purchaseDate +
                ", condition=" + condition +
                '}';
    }
}
