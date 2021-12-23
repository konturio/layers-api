package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * The extent of the features in the collection. In the Core only spatial and temporal extents are specified. Extensions may add additional members to represent other extents, for example, thermal or pressure ranges.
 */
@Schema(description = "The extent of the features in the collection. In the Core only spatial and temporal extents are specified. Extensions may add additional members to represent other extents, for example, thermal or pressure ranges.")
public class Extent {

    @JsonProperty("spatial")
    private ExtentSpatial spatial;

    @JsonProperty("temporal")
    private ExtentTemporal temporal;

    public Extent spatial(ExtentSpatial spatial) {
        this.spatial = spatial;
        return this;
    }

    @JsonProperty("spatial")
    @Schema(description = "")
    public ExtentSpatial getSpatial() {
        return spatial;
    }

    public void setSpatial(ExtentSpatial spatial) {
        this.spatial = spatial;
    }

    public Extent temporal(ExtentTemporal temporal) {
        this.temporal = temporal;
        return this;
    }

    @JsonProperty("temporal")
    @Schema(description = "")
    public ExtentTemporal getTemporal() {
        return temporal;
    }

    public void setTemporal(ExtentTemporal temporal) {
        this.temporal = temporal;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Extent extent = (Extent) o;
        return Objects.equals(this.spatial, extent.spatial) &&
                Objects.equals(this.temporal, extent.temporal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spatial, temporal);
    }

}
