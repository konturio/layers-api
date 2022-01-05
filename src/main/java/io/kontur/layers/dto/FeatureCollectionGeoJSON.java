package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeatureCollectionGeoJSON {

    @JsonProperty("type")
    @NotNull
    private TypeEnum type;
    @JsonProperty("features")
    @NotNull
    private List<FeatureGeoJSON> features;
    @JsonProperty("links")
    private List<Link> links;
    @JsonProperty("timeStamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime timeStamp;
    @JsonProperty("numberMatched")
    private Integer numberMatched;
    @JsonProperty("numberReturned")
    private Integer numberReturned;

    public FeatureCollectionGeoJSON() {
        this.features = new ArrayList<>();
        this.type = TypeEnum.FEATURECOLLECTION;
    }

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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public FeatureCollectionGeoJSON timeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public FeatureCollectionGeoJSON numberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

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
