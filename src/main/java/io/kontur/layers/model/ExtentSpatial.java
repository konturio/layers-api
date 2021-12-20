package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The spatial extent of the features in the collection.
 */
@Schema(description = "The spatial extent of the features in the collection.")
public class ExtentSpatial {

    @JsonProperty("bbox")
    private List<List<BigDecimal>> bbox;
    @JsonProperty("crs")
    private CrsEnum crs = CrsEnum.CRS84;

    public ExtentSpatial bbox(List<List<BigDecimal>> bbox) {
        this.bbox = bbox;
        return this;
    }

    public ExtentSpatial addBboxItem(List<BigDecimal> bboxItem) {
        if (this.bbox == null) {
            this.bbox = new ArrayList<>();
        }
        this.bbox.add(bboxItem);
        return this;
    }

    @JsonProperty("bbox")
    @Schema(description = "One or more bounding boxes that describe the spatial extent of the dataset. In the Core only a single bounding box is supported. Extensions may support additional areas. If multiple areas are provided, the union of the bounding boxes describes the spatial extent.")
    @Size(min = 1)
    public List<List<BigDecimal>> getBbox() {
        return bbox;
    }

    public void setBbox(List<List<BigDecimal>> bbox) {
        this.bbox = bbox;
    }

    public ExtentSpatial crs(CrsEnum crs) {
        this.crs = crs;
        return this;
    }

    @JsonProperty("crs")
    @Schema(description = "Coordinate reference system of the coordinates in the spatial extent (property `bbox`). The default reference system is WGS 84 longitude/latitude. In the Core this is the only supported coordinate reference system. Extensions may support additional coordinate reference systems and add additional enum values.")
    public CrsEnum getCrs() {
        return crs;
    }

    public void setCrs(CrsEnum crs) {
        this.crs = crs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtentSpatial extentSpatial = (ExtentSpatial) o;
        return Objects.equals(this.bbox, extentSpatial.bbox) &&
                Objects.equals(this.crs, extentSpatial.crs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bbox, crs);
    }

    /**
     * Coordinate reference system of the coordinates in the spatial extent (property &#x60;bbox&#x60;). The default reference system is WGS 84 longitude/latitude. In the Core this is the only supported coordinate reference system. Extensions may support additional coordinate reference systems and add additional enum values.
     */
    public enum CrsEnum {
        CRS84("http://www.opengis.net/def/crs/OGC/1.3/CRS84");

        private String value;

        CrsEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static CrsEnum fromValue(String text) {
            for (CrsEnum b : CrsEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }
}
