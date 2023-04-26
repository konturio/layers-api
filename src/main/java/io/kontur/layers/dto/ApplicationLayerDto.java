package io.kontur.layers.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLayerDto {

    private String layerId;
    private Boolean isDefault;
    private ObjectNode legendStyle;
    private ObjectNode displayRule;

}
