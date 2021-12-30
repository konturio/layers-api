package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MultipolygonGeoJSON
 */
public class MultiPolygonGeoJSON extends GeometryGeoJSON {

    @JsonProperty("coordinates")
    private List<List<List<List<BigDecimal>>>> coordinates;

    public MultiPolygonGeoJSON() {
        super(TypeEnum.MULTIPOLYGON);
        this.coordinates = new ArrayList<>();
    }

    public MultiPolygonGeoJSON coordinates(List<List<List<List<BigDecimal>>>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public MultiPolygonGeoJSON addCoordinatesItem(List<List<List<BigDecimal>>> coordinatesItem) {
        this.coordinates.add(coordinatesItem);
        return this;
    }

    @JsonProperty("coordinates")
    @Schema(required = true, description = "")
    @NotNull
    public List<List<List<List<BigDecimal>>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<List<BigDecimal>>>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiPolygonGeoJSON multipolygonGeoJSON = (MultiPolygonGeoJSON) o;
        return Objects.equals(this.getType(), multipolygonGeoJSON.getType()) &&
                Objects.equals(this.coordinates, multipolygonGeoJSON.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), coordinates);
    }
}
