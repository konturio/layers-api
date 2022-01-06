package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.wololo.geojson.Geometry;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeatureGeoJSON {

    @JsonProperty("type")
    private TypeEnum type;
    @JsonProperty("geometry")
    private Geometry geometry;
    @JsonProperty("properties")
    private Object properties;
    @JsonProperty("id")
    private String id;
    @JsonProperty("links")
    private List<Link> links;

    public FeatureGeoJSON type(TypeEnum type) {
        this.type = type;
        return this;
    }

    @JsonProperty("type")
    @Schema(required = true, description = "")
    @NotNull
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public FeatureGeoJSON geometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }

    @JsonProperty("geometry")
    @Schema(required = true, description = "")
    @NotNull
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public FeatureGeoJSON properties(Object properties) {
        this.properties = properties;
        return this;
    }

    @JsonProperty("properties")
    @Schema(required = true, description = "")
    @NotNull
    public Object getProperties() {
        return properties;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }

    public FeatureGeoJSON id(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("id")
    @Schema(description = "")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FeatureGeoJSON links(List<Link> links) {
        this.links = links;
        return this;
    }

    public FeatureGeoJSON addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(linksItem);
        return this;
    }

    @JsonProperty("links")
    @Schema(description = "")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureGeoJSON featureGeoJSON = (FeatureGeoJSON) o;
        return Objects.equals(this.type, featureGeoJSON.type) &&
                Objects.equals(this.geometry, featureGeoJSON.geometry) &&
                Objects.equals(this.properties, featureGeoJSON.properties) &&
                Objects.equals(this.id, featureGeoJSON.id) &&
                Objects.equals(this.links, featureGeoJSON.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, geometry, properties, id, links);
    }

    public enum TypeEnum {
        FEATURE("Feature");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }
}
