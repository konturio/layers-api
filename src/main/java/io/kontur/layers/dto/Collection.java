package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.repository.model.LayersCategoryProperties;
import io.kontur.layers.repository.model.LayersGroupProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Collection {

    @JsonProperty("id")
    @Schema(example = "address", required = true, description = "identifier of the collection used, for example, in URIs")
    @NotNull
    private String id;

    @JsonProperty("title")
    @Schema(example = "address", description = "human readable title of the collection")
    private String title;

    @JsonProperty("description")
    @Schema(example = "An address.", description = "a description of the features in the collection")
    private String description;

    @JsonProperty("copyrights")
    private String copyrights;

    @JsonProperty("properties")
    private ObjectNode properties;

    @JsonProperty("featureProperties")
    private ObjectNode featureProperties;

    @JsonProperty("legend")
    private ObjectNode legend;

    @JsonProperty("group")
    private LayersGroupProperties group;

    @JsonProperty("category")
    private LayersCategoryProperties category;

    @JsonProperty("links")
    @Schema(example = "[{\"href\":\"http://data.example.com/buildings\",\"rel\":\"item\"},{\"href\":\"http://example.com/concepts/buildings.html\",\"rel\":\"describedBy\",\"type\":\"text/html\"}]", required = true, description = "")
    @NotNull
    @Builder.Default()
    private List<Link> links = new ArrayList<>();

    @JsonProperty("extent")
    private Extent extent;

    @JsonProperty("itemType")
    @Schema(description = "indicator about the type of the items in the collection (the default value is 'feature').")
    @Builder.Default
    private String itemType = "feature";

    @JsonProperty("crs")
    @Schema(example = "[\"http://www.opengis.net/def/crs/OGC/1.3/CRS84\",\"http://www.opengis.net/def/crs/EPSG/0/4326\"]", description = "the list of coordinate reference systems supported by the service")
    private List<String> crs;

    @JsonProperty("ownedByUser")
    private boolean ownedByUser;

    @JsonProperty("styleRule")
    private ObjectNode styleRule;

    @JsonProperty("displayRule")
    private ObjectNode displayRule;
}
