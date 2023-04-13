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
    @JsonProperty("style")
    private ObjectNode style;
    @JsonProperty("popupConfig")
    private ObjectNode popupConfig;
    @JsonProperty("styleRule")
    private ObjectNode styleRule;
    @JsonProperty("displayRule")
    private ObjectNode displayRule;
    @JsonProperty("appId")
    private UUID appId;
    @JsonProperty("tileSize")
    private Integer tileSize;
    @JsonProperty("minZoom")
    private Integer minZoom;
    @JsonProperty("maxZoom")
    private Integer maxZoom;

    public enum Type {
        raster,
        vector,
        feature
    }
}
