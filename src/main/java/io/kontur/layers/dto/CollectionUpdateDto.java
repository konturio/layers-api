package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.validation.ValidGeoJSON;
import lombok.Data;
import org.wololo.geojson.Geometry;

import javax.validation.constraints.NotNull;
import java.util.UUID;

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
    @ValidGeoJSON
    private Geometry geometry;
    @JsonProperty("properties")
    private ObjectNode properties;
    @JsonProperty("featureProperties")
    private ObjectNode featureProperties;
    @JsonProperty("styleRule")
    private ObjectNode styleRule;
    @JsonProperty("appId")
    private UUID appId;

    public enum Type {
        tiles,
        feature
    }
}
