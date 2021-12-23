package io.kontur.layers.repository.model;

import io.kontur.layers.dto.DateTimeRange;

import java.time.OffsetDateTime;


public class Layer {

    private Long id;
    private String publicId;
    private String name;
    private String description;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime sourceLastUpdated;
    private String spatialExtent;
    private DateTimeRange temporalExtent;
    private Integer numberMatched;

    public Layer() {
    }

    public Layer(final String publicId,
                 final String name,
                 final String description,
                 final OffsetDateTime lastUpdated,
                 final OffsetDateTime sourceLastUpdated,
                 final String spatialExtent,
                 final DateTimeRange temporalExtent,
                 final Integer numberMatched) {
        this.publicId = publicId;
        this.name = name;
        this.description = description;
        this.lastUpdated = lastUpdated;
        this.sourceLastUpdated = sourceLastUpdated;
        this.spatialExtent = spatialExtent;
        this.temporalExtent = temporalExtent;
        this.numberMatched = numberMatched;
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
}
