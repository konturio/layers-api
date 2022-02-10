package io.kontur.layers.dto;

import io.kontur.layers.dto.validation.ValidGeoJSON;
import org.wololo.geojson.Geometry;

import javax.validation.constraints.Min;

public class CollectionsSearchDto {

    @ValidGeoJSON
    private Geometry geometry;
    @Min(1)
    private Integer limit = 10;
    @Min(0)
    private Integer offset = 0;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
