package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeometryCollectionGeoJSON extends GeometryGeoJSON {

    @JsonProperty("geometries")
    private List<GeometryGeoJSON> geometries;

    public GeometryCollectionGeoJSON() {
        super(TypeEnum.GEOMETRYCOLLECTION);
        this.geometries = new ArrayList<>();
    }


    public GeometryCollectionGeoJSON geometries(List<GeometryGeoJSON> geometries) {
        this.geometries = geometries;
        return this;
    }

    public GeometryCollectionGeoJSON addGeometriesItem(GeometryGeoJSON geometriesItem) {
        this.geometries.add(geometriesItem);
        return this;
    }

    @JsonProperty("geometries")
    @Schema(required = true, description = "")
    @NotNull
    public List<GeometryGeoJSON> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<GeometryGeoJSON> geometries) {
        this.geometries = geometries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeometryCollectionGeoJSON geometrycollectionGeoJSON = (GeometryCollectionGeoJSON) o;
        return Objects.equals(this.getType(), geometrycollectionGeoJSON.getType()) &&
                Objects.equals(this.geometries, geometrycollectionGeoJSON.geometries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), geometries);
    }
}
