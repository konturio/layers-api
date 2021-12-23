package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Collection {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("links")
    private List<Link> links = new ArrayList<>();

    @JsonProperty("extent")
    private Extent extent;

    @JsonProperty("itemType")
    private String itemType = "feature";

    @JsonProperty("crs")
    private List<String> crs;

    public Collection id(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("id")
    @Schema(example = "address", required = true, description = "identifier of the collection used, for example, in URIs")
    @NotNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("title")
    @Schema(example = "address", description = "human readable title of the collection")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("description")
    @Schema(example = "An address.", description = "a description of the features in the collection")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection links(List<Link> links) {
        this.links = links;
        return this;
    }

    public Collection addLinksItem(Link linksItem) {
        this.links.add(linksItem);
        return this;
    }

    @JsonProperty("links")
    @Schema(example = "[{\"href\":\"http://data.example.com/buildings\",\"rel\":\"item\"},{\"href\":\"http://example.com/concepts/buildings.html\",\"rel\":\"describedBy\",\"type\":\"text/html\"}]", required = true, description = "")
    @NotNull
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Collection extent(Extent extent) {
        this.extent = extent;
        return this;
    }

    @JsonProperty("extent")
    @Schema(description = "")
    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public Collection itemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    @JsonProperty("itemType")
    @Schema(description = "indicator about the type of the items in the collection (the default value is 'feature').")
    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Collection crs(List<String> crs) {
        this.crs = crs;
        return this;
    }

    public Collection addCrsItem(String crsItem) {
        if (this.crs == null) {
            this.crs = new ArrayList<String>();
        }
        this.crs.add(crsItem);
        return this;
    }

    @JsonProperty("crs")
    @Schema(example = "[\"http://www.opengis.net/def/crs/OGC/1.3/CRS84\",\"http://www.opengis.net/def/crs/EPSG/0/4326\"]", description = "the list of coordinate reference systems supported by the service")
    public List<String> getCrs() {
        return crs;
    }

    public void setCrs(List<String> crs) {
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
        Collection collection = (Collection) o;
        return Objects.equals(this.id, collection.id) &&
                Objects.equals(this.title, collection.title) &&
                Objects.equals(this.description, collection.description) &&
                Objects.equals(this.links, collection.links) &&
                Objects.equals(this.extent, collection.extent) &&
                Objects.equals(this.itemType, collection.itemType) &&
                Objects.equals(this.crs, collection.crs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, links, extent, itemType, crs);
    }
}
