package io.kontur.layers.repository.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApplicationLayer {

    private UUID appId;
    private String layerId;
    private Boolean isDefault;
    private ObjectNode styleRule;
    private ObjectNode displayRule;

}
