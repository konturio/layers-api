package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PointGeoJSON.class, name = "Point"),
        @JsonSubTypes.Type(value = LineStringGeoJSON.class, name = "LineString"),
        @JsonSubTypes.Type(value = PolygonGeoJSON.class, name = "Polygon"),
        @JsonSubTypes.Type(value = MultiPointGeoJSON.class, name = "MultiPoint"),
        @JsonSubTypes.Type(value = MultiLineStringGeoJSON.class, name = "MultiLineString"),
        @JsonSubTypes.Type(value = MultiPolygonGeoJSON.class, name = "MultiPolygon"),
        @JsonSubTypes.Type(value = FeatureGeoJSON.class, name = "Feature"),
        @JsonSubTypes.Type(value = FeatureCollectionGeoJSON.class, name = "FeatureCollection"),
        @JsonSubTypes.Type(value = GeometryCollectionGeoJSON.class, name = "GeometryCollection")
})
public abstract class GeometryGeoJSON {

    @JsonProperty("type")
    private TypeEnum type;

    protected GeometryGeoJSON(TypeEnum type) {
        this.type = type;
    }

    @JsonProperty("type")
    @Schema(required = true, description = "")
    @NotNull
    public TypeEnum getType() {
        return type;
    }

    /**
     * Gets or Sets type
     */
    public enum TypeEnum {

        GEOMETRYCOLLECTION("GeometryCollection"),
        LINESTRING("LineString"),
        MULTILINESTRING("MultiLineString"),
        MULTIPOINT("MultiPoint"),
        MULTIPOLYGON("MultiPolygon"),
        POINT("Point"),
        POLYGON("Polygon");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
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
