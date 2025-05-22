package io.kontur.layers.dto;

import io.kontur.layers.dto.validation.ValidGeoJSON;
import lombok.Getter;
import lombok.Setter;
import org.wololo.geojson.Geometry;

import javax.validation.constraints.Min;
import java.util.UUID;

import io.kontur.layers.dto.SortOrder;

@Getter
@Setter
public class CollectionsItemsSearchDto {

    @ValidGeoJSON
    private Geometry geometry;
    private UUID appId;
    @Min(1)
    private Integer limit = 10;
    @Min(0)
    private Integer offset = 0;
    private DateTimeRange datetime;
    private SortOrder order = SortOrder.ASC;
}
