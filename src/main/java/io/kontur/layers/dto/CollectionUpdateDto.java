package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.repository.model.Layer;
import lombok.Data;
import org.wololo.geojson.Geometry;

@Data
public class CollectionUpdateDto {

    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("link")
    private Link link;
    @JsonProperty("itemType")
    private Type itemType = Type.feature;
    @JsonProperty("copyrights")
    private String copyrights;
    @JsonProperty("geometry")
    private Geometry geometry;
    @JsonProperty("properties")
    private ObjectNode properties;
    @JsonProperty("legend")
    private ObjectNode legend;

    public enum Type {
        tiles,
        feature
    }
}
