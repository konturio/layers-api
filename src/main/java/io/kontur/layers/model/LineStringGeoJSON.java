package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineStringGeoJSON extends GeometryGeoJSON {

    @JsonProperty("coordinates")
    private List<List<BigDecimal>> coordinates;

    public LineStringGeoJSON() {
        super(TypeEnum.LINESTRING);
        this.coordinates = new ArrayList<>();
    }

    public LineStringGeoJSON coordinates(List<List<BigDecimal>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public LineStringGeoJSON addCoordinatesItem(List<BigDecimal> coordinatesItem) {
        this.coordinates.add(coordinatesItem);
        return this;
    }

    @JsonProperty("coordinates")
    @Schema(required = true, description = "")
    @NotNull
    @Size(min = 2)
    public List<List<BigDecimal>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<BigDecimal>> coordinates) {
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
        LineStringGeoJSON linestringGeoJSON = (LineStringGeoJSON) o;
        return Objects.equals(this.getType(), linestringGeoJSON.getType()) &&
                Objects.equals(this.coordinates, linestringGeoJSON.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), coordinates);
    }
}
