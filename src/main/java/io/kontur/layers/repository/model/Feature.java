package io.kontur.layers.repository.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.DateTimeRange;

import java.time.OffsetDateTime;

public class Feature {

    private Long layerId;
    private String featureId;
    private String geometry;
    private ObjectNode properties;
    private OffsetDateTime lastUpdated;
    private Integer numberMatched;

    public Feature() {
    }

    public Feature(final Long layerId, final String featureId, final String geometry, final ObjectNode properties,
                   final OffsetDateTime lastUpdated
    ) {
        this.layerId = layerId;
        this.featureId = featureId;
        this.geometry = geometry;
        this.properties = properties;
        this.lastUpdated = lastUpdated;
    }

    public Long getLayerId() {
        return layerId;
    }

    public String getFeatureId() {
        return featureId;
    }

    public String getGeometry() {
        return geometry;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public Integer getNumberMatched() {
        return numberMatched;
    }

    public void setLayerId(final Long layerId) {
        this.layerId = layerId;
    }

    public void setFeatureId(final String featureId) {
        this.featureId = featureId;
    }

    public void setGeometry(final String geometry) {
        this.geometry = geometry;
    }

    public void setProperties(final ObjectNode properties) {
        this.properties = properties;
    }

    public void setLastUpdated(final OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setNumberMatched(final Integer numberMatched) {
        this.numberMatched = numberMatched;
    }
}
