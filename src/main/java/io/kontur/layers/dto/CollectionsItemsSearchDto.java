package io.kontur.layers.dto;

import javax.validation.constraints.Min;

public class CollectionsItemsSearchDto {

    private GeometryGeoJSON geometry;
    @Min(1)
    private Integer limit = 10;
    @Min(0)
    private Integer offset = 0;
    private DateTimeRange datetime;

    public GeometryGeoJSON getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryGeoJSON geometry) {
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

    public DateTimeRange getDatetime() {
        return datetime;
    }

    public void setDatetime(DateTimeRange datetime) {
        this.datetime = datetime;
    }
}
