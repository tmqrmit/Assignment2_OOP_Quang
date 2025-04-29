package app.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class EquipmentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId; // Primary key for EquipmentImage

    @OneToOne(optional = false) // Creates a foreign key to the Equipment table
    @JoinColumn(name = "equipment_id", nullable = false, unique = true) // Reference to equipment table `id`
    private Equipment equipment; // Reference to the Equipment object

    @Lob
    private byte[] imageData; // Binary image data

    private String fileName; // File name of the image (e.g., "example.png")

    private String fileType; // MIME type (e.g., "image/png")

    // Default constructor
    public EquipmentImage() {}

    // Constructor
    public EquipmentImage(Equipment equipment, byte[] imageData, String fileName, String fileType) {
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment cannot be null.");
        }
        if (imageData == null || imageData.length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty.");
        }
        this.equipment = equipment;
        this.imageData = imageData;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    // Getters and Setters
    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipmentImage that)) return false;
        return Objects.equals(imageId, that.imageId) &&
                Objects.equals(equipment, that.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, equipment);
    }

    @Override
    public String toString() {
        return "EquipmentImage{" +
                "imageId=" + imageId +
                ", equipment=" + equipment +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}