package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeatureCollectionGeoJSON {

    @JsonProperty("type")
    private TypeEnum type;
    @JsonProperty("features")
    private List<FeatureGeoJSON> features;
    @JsonProperty("links")
    private List<Link> links;
    @JsonProperty("timeStamp")
    private String timeStamp;
    @JsonProperty("numberMatched")
    private Integer numberMatched;
    @JsonProperty("numberReturned")
    private Integer numberReturned;

    public FeatureCollectionGeoJSON() {
        this.features = new ArrayList<>();
        this.type = TypeEnum.FEATURECOLLECTION;
    }

    @JsonProperty("type")
    @Schema(required = true, description = "")
    @NotNull
    public TypeEnum getType() {
        return type;
    }

    public FeatureCollectionGeoJSON features(List<FeatureGeoJSON> features) {
        this.features = features;
        return this;
    }

    public FeatureCollectionGeoJSON addFeaturesItem(FeatureGeoJSON featuresItem) {
        this.features.add(featuresItem);
        return this;
    }

    @JsonProperty("features")
    @Schema(required = true, description = "")
    @NotNull
    public List<FeatureGeoJSON> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureGeoJSON> features) {
        this.features = features;
    }

    public FeatureCollectionGeoJSON links(List<Link> links) {
        this.links = links;
        return this;
    }

    public FeatureCollectionGeoJSON addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(linksItem);
        return this;
    }

    @JsonProperty("links")
    @Schema(description = "")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public FeatureCollectionGeoJSON timeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    @JsonProperty("timeStamp")
    @Schema(description = "This property indicates the time and date when the response was generated.")
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public FeatureCollectionGeoJSON numberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

    @JsonProperty("numberMatched")
    @Schema(description = "The number of features of the feature type that match the selection parameters like `bbox`.")
    public Integer getNumberMatched() {
        return numberMatched;
    }

    public void setNumberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    public FeatureCollectionGeoJSON numberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

    @JsonProperty("numberReturned")
    @Schema(description = "The number of features in the feature collection.  A server may omit this information in a response, if the information about the number of features is not known or difficult to compute.  If the value is provided, the value shall be identical to the number of items in the \"features\" array.")
    public Integer getNumberReturned() {
        return numberReturned;
    }

    public void setNumberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureCollectionGeoJSON featureCollectionGeoJSON = (FeatureCollectionGeoJSON) o;
        return Objects.equals(this.type, featureCollectionGeoJSON.type) &&
                Objects.equals(this.features, featureCollectionGeoJSON.features) &&
                Objects.equals(this.links, featureCollectionGeoJSON.links) &&
                Objects.equals(this.timeStamp, featureCollectionGeoJSON.timeStamp) &&
                Objects.equals(this.numberMatched, featureCollectionGeoJSON.numberMatched) &&
                Objects.equals(this.numberReturned, featureCollectionGeoJSON.numberReturned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, features, links, timeStamp, numberMatched, numberReturned);
    }

    /**
     * Gets or Sets type
     */
    public enum TypeEnum {
        FEATURECOLLECTION("FeatureCollection");

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
