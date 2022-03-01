package io.kontur.layers.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    private UUID id;
    private Boolean showAllPublicLayers;
    private Boolean isPublic;
    private String owner;

}
