package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiPointGeoJSON extends GeometryGeoJSON {

    @JsonProperty("coordinates")
    private List<List<BigDecimal>> coordinates;

    public MultiPointGeoJSON() {
        super(TypeEnum.MULTIPOINT);
        this.coordinates = new ArrayList<>();
    }

    public MultiPointGeoJSON coordinates(List<List<BigDecimal>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public MultiPointGeoJSON addCoordinatesItem(List<BigDecimal> coordinatesItem) {
        this.coordinates.add(coordinatesItem);
        return this;
    }
    
    @JsonProperty("coordinates")
    @Schema(required = true, description = "")
    @NotNull
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
        MultiPointGeoJSON multipointGeoJSON = (MultiPointGeoJSON) o;
        return Objects.equals(this.getType(), multipointGeoJSON.getType()) &&
                Objects.equals(this.coordinates, multipointGeoJSON.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), coordinates);
    }
}
