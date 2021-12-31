package io.kontur.layers.repository.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.dto.GeometryGeoJSON;

import java.time.OffsetDateTime;


public class Layer {

    private Long id;
    private String publicId;
    private String name;
    private String description;
    private String geometry;
    private String copyrights;
    private ObjectNode properties;
    private ObjectNode legend;
    private ObjectNode group;
    private ObjectNode category;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime sourceLastUpdated;
    private String spatialExtent;
    private DateTimeRange temporalExtent;
    private Integer numberMatched;
    private boolean isPublic;
    private String owner;

    public Layer() {
    }

    public Layer(String publicId, String name, String description, String geometry, String copyrights,
                 ObjectNode properties, ObjectNode legend, ObjectNode group,
                 ObjectNode category, OffsetDateTime lastUpdated, OffsetDateTime sourceLastUpdated,
                 String spatialExtent, DateTimeRange temporalExtent, Integer numberMatched, boolean isPublic,
                 String owner) {
        this.publicId = publicId;
        this.name = name;
        this.description = description;
        this.geometry = geometry;
        this.copyrights = copyrights;
        this.properties = properties;
        this.legend = legend;
        this.group = group;
        this.category = category;
        this.lastUpdated = lastUpdated;
        this.sourceLastUpdated = sourceLastUpdated;
        this.spatialExtent = spatialExtent;
        this.temporalExtent = temporalExtent;
        this.numberMatched = numberMatched;
        this.isPublic = isPublic;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getGeometry() {
        return geometry;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public ObjectNode getLegend() {
        return legend;
    }

    public ObjectNode getGroup() {
        return group;
    }

    public ObjectNode getCategory() {
        return category;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public OffsetDateTime getSourceLastUpdated() {
        return sourceLastUpdated;
    }

    public String getSpatialExtent() {
        return spatialExtent;
    }

    public DateTimeRange getTemporalExtent() {
        return temporalExtent;
    }

    public Integer getNumberMatched() {
        return numberMatched;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getOwner() {
        return owner;
    }
}
