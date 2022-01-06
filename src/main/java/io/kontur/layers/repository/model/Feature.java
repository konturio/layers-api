package io.kontur.layers.repository.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wololo.geojson.Geometry;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feature {

    private Long layerId;
    private String featureId;
    private Geometry geometry;
    private ObjectNode properties;
    private OffsetDateTime lastUpdated;

}
