package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PointGeoJSON extends GeometryGeoJSON {

    @JsonProperty("coordinates")
    private List<BigDecimal> coordinates;

    public PointGeoJSON() {
        super(TypeEnum.POINT);
        this.coordinates = new ArrayList<>();
    }

    public PointGeoJSON coordinates(List<BigDecimal> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public PointGeoJSON addCoordinatesItem(BigDecimal coordinatesItem) {
        this.coordinates.add(coordinatesItem);
        return this;
    }

    @JsonProperty("coordinates")
    @Schema(required = true, description = "")
    @NotNull
    @Size(min = 2)
    public List<BigDecimal> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<BigDecimal> coordinates) {
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
        PointGeoJSON pointGeoJSON = (PointGeoJSON) o;
        return Objects.equals(getType(), pointGeoJSON.getType()) &&
                Objects.equals(this.coordinates, pointGeoJSON.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), coordinates);
    }
}
