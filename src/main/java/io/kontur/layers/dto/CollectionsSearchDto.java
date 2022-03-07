package io.kontur.layers.dto;

import io.kontur.layers.dto.validation.ValidGeoJSON;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.wololo.geojson.Geometry;

import javax.validation.constraints.Min;
import java.util.UUID;

@Getter
@Setter
public class CollectionsSearchDto {

    @ValidGeoJSON
    private Geometry geometry;
    @Schema(defaultValue = "ANY")
    private CollectionOwner collectionOwner = CollectionOwner.ANY;
    private UUID appId;
    @Min(1)
    @Schema(defaultValue = "10")
    private Integer limit = 10;
    @Min(0)
    @Schema(defaultValue = "0")
    private Integer offset = 0;

}
