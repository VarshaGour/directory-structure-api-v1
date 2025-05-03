package directory.structure.api.util;

import java.util.List;
import java.util.stream.Collectors;

public enum Classification {
    PUBLIC("Public"),
    SECRET("Secret"),
    TOP_SECRET("Top secret");

    private final String value;

    Classification(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Classification fromValue(String value) {
        for (Classification classification : Classification.values()) {
            if (classification.value.equalsIgnoreCase(value)) {
                return classification;
            }
        }
        throw new IllegalArgumentException("Unknown classification: " + value);
    }

    public static List<Classification> fromValues(List<String> values) {
        return values.stream()
                .map(Classification::fromValue)
                .collect(Collectors.toList());
    }
}