package io.kontur.layers.dto;

public class FeaturePropertiesFilter {

    private String fieldName;
    private String[] pattern;

    public FeaturePropertiesFilter(final String fieldName, final String[] pattern) {
        this.fieldName = fieldName;
        this.pattern = pattern;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String[] getPattern() {
        return pattern;
    }

}
