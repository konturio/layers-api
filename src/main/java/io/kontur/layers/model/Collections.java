package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class Collections {

    @JsonProperty("links")
    private List<Link> links = new ArrayList<Link>();

    @JsonProperty("collections")
    private List<Collection> collections = new ArrayList<Collection>();
    @JsonProperty("numberMatched")
    private Integer numberMatched;
    @JsonProperty("numberReturned")
    private Integer numberReturned;

    public Collections links(List<Link> links) {
        this.links = links;
        return this;
    }

    public Collections addLinksItem(Link linksItem) {
        this.links.add(linksItem);
        return this;
    }

    @JsonProperty("links")
    @Schema(required = true, description = "")
    @NotNull
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Collections collections(List<Collection> collections) {
        this.collections = collections;
        return this;
    }
    public Collections numberReturned(Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

    public Collections numberMatched(Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

    public Collections addCollectionsItem(Collection collectionsItem) {
        this.collections.add(collectionsItem);
        return this;
    }

    @JsonProperty("collections")
    @Schema(required = true, description = "")
    @NotNull
    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public Integer getNumberMatched() {
        return numberMatched;
    }

    public Integer getNumberReturned() {
        return numberReturned;
    }
}
