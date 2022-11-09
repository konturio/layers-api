package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationUpdateDto {

    private boolean showAllPublicLayers;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private List<ApplicationLayerDto> layers;
    private String name;
    private String iconUrl;
}
