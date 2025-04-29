package app.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}"; // Return an empty set representation
        }
        return "{" + String.join(", ", attribute) + "}";
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        // Convert the String in the format {E005, E004} to a Set<String>
        if (dbData == null || dbData.isEmpty() || dbData.equals("{}")) {
            return new HashSet<>(); // Return an empty Set if no data present
        }
        String cleanedData = dbData.replace("{", "").replace("}", "").trim(); // Remove { and }
        if (cleanedData.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(cleanedData.split(",\\s*"))); // Split on comma and trim spaces
    }
}