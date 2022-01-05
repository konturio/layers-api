package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.repository.model.Layer;
import lombok.Data;

@Data
public class CollectionUpdateDto {

    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("link")
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
}
