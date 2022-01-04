package io.kontur.layers.repository.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.DateTimeRange;

import java.time.OffsetDateTime;


public class Layer {

    private Long id;
    private String publicId;
    private String name;
    private String description;
    private String url;
    private Type type;
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

    public Layer(String publicId, String name, String description, String url, Layer.Type type, String geometry, String copyrights,
                 ObjectNode properties, ObjectNode legend, ObjectNode group,
                 ObjectNode category, OffsetDateTime lastUpdated, OffsetDateTime sourceLastUpdated,
                 String spatialExtent, DateTimeRange temporalExtent, Integer numberMatched, boolean isPublic,
                 String owner) {
        this.publicId = publicId;
        this.name = name;
        this.description = description;
        this.url = url;
        this.type = type;
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

    public String getUrl() {
        return url;
    }

    public Type getType() {
        return type;
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

    public static class Builder {

        private String publicId;
        private String name;
        private String description;
        private String url;
        private Type type;
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

        public Builder publicId(String publicId) {
            this.publicId = publicId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder geometry(String geometry) {
            this.geometry = geometry;
            return this;
        }

        public Builder copyrights(String copyrights) {
            this.copyrights = copyrights;
            return this;
        }

        public Builder properties(ObjectNode properties) {
            this.properties = properties;
            return this;
        }

        public Builder legend(ObjectNode legend) {
            this.legend = legend;
            return this;
        }

        public Builder group(ObjectNode group) {
            this.group = group;
            return this;
        }

        public Builder category(ObjectNode category) {
            this.category = category;
            return this;
        }

        public Builder lastUpdated(OffsetDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder sourceLastUpdated(OffsetDateTime sourceLastUpdated) {
            this.sourceLastUpdated = sourceLastUpdated;
            return this;
        }

        public Builder spatialExtent(String spatialExtent) {
            this.spatialExtent = spatialExtent;
            return this;
        }

        public Builder temporalExtent(DateTimeRange temporalExtent) {
            this.temporalExtent = temporalExtent;
            return this;
        }

        public Builder numberMatched(Integer numberMatched) {
            this.numberMatched = numberMatched;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Layer createLayer() {
            return new Layer(publicId, name, description, url, type, geometry, copyrights, properties, legend, group,
                    category, lastUpdated, sourceLastUpdated, spatialExtent, temporalExtent, numberMatched, isPublic,
                    owner);
        }
    }

    public enum Type {
        tiles,
        feature
    }
}
