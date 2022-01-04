package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.repository.model.Layer;

public class CollectionUpdateDto {

    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("links")
    private Link link;
    @JsonProperty("itemType")
    private Layer.Type itemType = Layer.Type.feature;
    @JsonProperty("copyrights")
    private String copyrights;
    @JsonProperty("geometry")
    private GeometryGeoJSON geometry;
    @JsonProperty("properties")
    private ObjectNode properties;
    @JsonProperty("legend")
    private ObjectNode legend;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Layer.Type getItemType() {
        return itemType;
    }

    public void setItemType(Layer.Type itemType) {
        this.itemType = itemType;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public GeometryGeoJSON getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryGeoJSON geometry) {
        this.geometry = geometry;
    }

    public ObjectNode getProperties() {
        return properties;
    }

    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    public ObjectNode getLegend() {
        return legend;
    }

    public void setLegend(ObjectNode legend) {
        this.legend = legend;
    }
}
