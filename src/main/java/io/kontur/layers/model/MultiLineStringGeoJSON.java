package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiLineStringGeoJSON extends GeometryGeoJSON {

    @JsonProperty("coordinates")
    private List<List<List<BigDecimal>>> coordinates;

    public MultiLineStringGeoJSON() {
        super(TypeEnum.MULTILINESTRING);
        this.coordinates = new ArrayList<>();
    }

    public MultiLineStringGeoJSON coordinates(List<List<List<BigDecimal>>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public MultiLineStringGeoJSON addCoordinatesItem(List<List<BigDecimal>> coordinatesItem) {
        this.coordinates.add(coordinatesItem);
        return this;
    }

    /**
     * Get coordinates
     *
     * @return coordinates
     **/
    @JsonProperty("coordinates")
    @Schema(required = true, description = "")
    @NotNull
    public List<List<List<BigDecimal>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<BigDecimal>>> coordinates) {
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
        MultiLineStringGeoJSON multilinestringGeoJSON = (MultiLineStringGeoJSON) o;
        return Objects.equals(this.getType(), multilinestringGeoJSON.getType()) &&
                Objects.equals(this.coordinates, multilinestringGeoJSON.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), coordinates);
    }
}
