package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.wololo.geojson.Geometry;

import javax.validation.constraints.NotNull;

@Data
public class CollectionUpdateDto {

    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("link")
    private Link link;
    @JsonProperty("itemType")
    @NotNull
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
