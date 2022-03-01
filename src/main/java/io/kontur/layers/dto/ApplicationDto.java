package io.kontur.layers.dto;

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
    private Boolean showAllPublicLayers;
    private Boolean isPublic;
    private List<Collection> defaultCollections;

}
