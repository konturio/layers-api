package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The temporal extent of the features in the collection.
 */
@Schema(description = "The temporal extent of the features in the collection.")
public class ExtentTemporal {

    @JsonProperty("interval")
    private List<List<String>> interval;
    @JsonProperty("trs")
    private TrsEnum trs = TrsEnum.GREGORIAN;

    public ExtentTemporal interval(List<List<String>> interval) {
        this.interval = interval;
        return this;
    }

    public ExtentTemporal addIntervalItem(List<String> intervalItem) {
        if (this.interval == null) {
            this.interval = new ArrayList<List<String>>();
        }
        this.interval.add(intervalItem);
        return this;
    }

    /**
     * One or more time intervals that describe the temporal extent of the dataset. The value &#x60;null&#x60; is supported and indicates an open time intervall. In the Core only a single time interval is supported. Extensions may support multiple intervals. If multiple intervals are provided, the union of the intervals describes the temporal extent.
     *
     * @return interval
     **/
    @JsonProperty("interval")
    @Schema(description = "One or more time intervals that describe the temporal extent of the dataset. The value `null` is supported and indicates an open time intervall. In the Core only a single time interval is supported. Extensions may support multiple intervals. If multiple intervals are provided, the union of the intervals describes the temporal extent.")
    @Size(min = 1)
    public List<List<String>> getInterval() {
        return interval;
    }

    public void setInterval(List<List<String>> interval) {
        this.interval = interval;
    }

    public ExtentTemporal trs(TrsEnum trs) {
        this.trs = trs;
        return this;
    }

    /**
     * Coordinate reference system of the coordinates in the temporal extent (property &#x60;interval&#x60;). The default reference system is the Gregorian calendar. In the Core this is the only supported temporal reference system. Extensions may support additional temporal reference systems and add additional enum values.
     *
     * @return trs
     **/
    @JsonProperty("trs")
    @Schema(description = "Coordinate reference system of the coordinates in the temporal extent (property `interval`). The default reference system is the Gregorian calendar. In the Core this is the only supported temporal reference system. Extensions may support additional temporal reference systems and add additional enum values.")
    public TrsEnum getTrs() {
        return trs;
    }

    public void setTrs(TrsEnum trs) {
        this.trs = trs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtentTemporal extentTemporal = (ExtentTemporal) o;
        return Objects.equals(this.interval, extentTemporal.interval) &&
                Objects.equals(this.trs, extentTemporal.trs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, trs);
    }

    /**
     * Coordinate reference system of the coordinates in the temporal extent (property &#x60;interval&#x60;). The default reference system is the Gregorian calendar. In the Core this is the only supported temporal reference system. Extensions may support additional temporal reference systems and add additional enum values.
     */
    public enum TrsEnum {
        GREGORIAN("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian");

        private String value;

        TrsEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static TrsEnum fromValue(String text) {
            for (TrsEnum b : TrsEnum.values()) {
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
