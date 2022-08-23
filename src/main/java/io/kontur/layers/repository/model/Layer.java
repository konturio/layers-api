package io.kontur.layers.repository.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Layer {

    private Long id;
    private String publicId;
    private String name;
    private String description;
    private String url;
    private String apiKey;
    private String type;
    private String geometry;
    private String copyrights;
    private ObjectNode properties;
    private ObjectNode styleRule;
    private ObjectNode displayRule;
    private ObjectNode featureProperties;
    private LayersGroupProperties group;
    private LayersCategoryProperties category;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime sourceLastUpdated;
    private String spatialExtent;
    private DateTimeRange temporalExtent;
    private boolean isPublic;
    private boolean isVisible;
    private String owner;
}
