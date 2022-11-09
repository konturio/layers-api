package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {

    private UUID id;
    private boolean showAllPublicLayers;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private List<Collection> defaultCollections;
    private String name;
    private String iconUrl;

}
